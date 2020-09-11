package com.java.heyuze;

import android.renderscript.ScriptC;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.invoke.VolatileCallSite;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

import com.alibaba.fastjson.*;
import com.huaban.analysis.jieba.*;

// InfoManager类public接口列表
///////////////////////////////////////
/*
        public static InfoManager getInstance(); // 获得实例

        public boolean hasInfectData(); // 判断疫情信息是否加载完毕
        public boolean hasNewsData(); // 判断新闻信息是否加载完毕
        public boolean isReadyForSearch(); // 判断搜索功能是否加载完毕

        public void loadJieba(); // 加载Jieba分词器
        public void loadJSON(InfoType type, String path); // 加载被本地JSON文件
        public void saveJSON(String path, String buf); // 保存本地JSON文件
        public String updateCovidData(InfoType type); // 通过HTTP更新疫情数据

        public Vector<InfectData> getInfectData(); // 获得加载好的疫情数据
        public Vector<NewsData> getNewsData(NewsData.NewsType type); // 获得加载好的新闻
        public Vector<KnowledgeData> getKnowledge(String key); // 通过HTTP获得某关键词的知识图谱
        public NewsContent getNewsContent(String id); // 通过HTTP获得新闻正文
        public Vector<EventsVector> getEventsList(); // 通过HTTP获得event列表
        public String[] getEventKeyword(int i); // 获得分类关键词

        public void loadScholarData(); // 加载知疫学者信息（很快，开应用后台秒加载）
        public Vector<ScholarData> getScholarData(ScholarData.ScholarType type); // 获得知疫学者信息

        public Vector<NewsData> searchNewsData(NewsData.NewsType type, String text); // 根据标题搜索新闻

*/
///////////////////////////////////////

public class InfoManager
{
    public enum InfoType
    {INFECTDATA, NEWSDATA, SCHOLARDATA}

    public boolean dictNeedUpdate = false;

    private static InfoManager instance = null;
    private JSONObject infectJSON = null;
    private JSONObject newsJSON = null;
    private JSONObject scholarJSON = null;

    private Vector<InfectData> infectData = null;
    private Vector<NewsData> newsData = null;
    private Vector<ScholarData> scholarData = null;

    private NewsDictionary newsDict = null;
    JiebaSegmenter segmenter = null;

    private InfoManager()
    {
    }

    public static InfoManager getInstance()
    {
        if (instance == null) instance = new InfoManager();
        return instance;
    }

    public boolean hasInfectData()
    {
        return infectData != null;
    }

    public boolean hasNewsData()
    {
        return newsData != null;
    }

    public boolean hasScholarData()
    {
        return scholarData != null;
    }

    public boolean isReadyForSearch()
    {
        return newsDict != null;
    }

    public void loadJieba()
    {
        segmenter = new JiebaSegmenter();
    }

    public void loadJSON(InfoType type, String path) throws Exception
    {
        File file = new File(path);
        if (!file.exists())
        {
            System.out.println("Cannot load local JSON!");
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        String content;
        while ((content = bufferedReader.readLine()) != null)
        {
            stringBuffer.append(content);
            stringBuffer.append("\r\n");
        }
        bufferedReader.close();
        switch (type)
        {
            case INFECTDATA:
                infectJSON = JSON.parseObject(stringBuffer.toString());
                break;
            case NEWSDATA:
                newsJSON = JSON.parseObject(stringBuffer.toString());
                break;
            default:
        }
        loadData(type);
        System.out.println("Load local JSON Success!");
        System.out.println("JSON Content: " + stringBuffer.toString());
    }

    public void saveJSON(String path, String buf) throws IOException
    {
        File file = new File(path);
        if (!file.exists()) file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
        writer.write(buf);
        writer.flush();
        writer.close();
    }

    private String getJSON(String urlString) throws Exception
    {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(60000);
        connection.connect();
        if (connection.getResponseCode() == 200)
        {
            InputStream stream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String content;
            while ((content = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(content);
                stringBuffer.append("\r\n");
            }
            bufferedReader.close();
            return stringBuffer.toString();
        }
        return null;
    }

    public void loadDictionary()
    {
        NewsDictionary resDict = new NewsDictionary();
        for (NewsData data : newsData)
            resDict.addTitle(data.title, data, segmenter);
        resDict.setNewsNum(newsData.size());
        synchronized (this)
        {
            newsDict = resDict;
        }
    }

    private void loadData(InfoType type)
    {
        switch (type)
        {
            case INFECTDATA:
                Vector<InfectData> resInfect = new Vector<>();
                for (String key : infectJSON.keySet())
                {
                    InfectData data = new InfectData();
                    String[] stringSet = key.split("\\|");
                    for (String entry : stringSet)
                        data.location.add(entry);
                    JSONObject content = infectJSON.getJSONObject(key);
                    JSONArray dayInfo = content.getJSONArray("data");
                    data.beginDate = content.getString("begin");
                    for (int i = 0; i < dayInfo.size(); ++i)
                    {
                        JSONArray detail = dayInfo.getJSONArray(i);
                        data.confirmed.add(detail.getIntValue(0));
                        data.suspected.add(detail.getIntValue(1));
                        data.cured.add(detail.getIntValue(2));
                        data.dead.add(detail.getIntValue(3));
                    }
                    resInfect.add(data);
                }
                Collections.sort(resInfect, new Comparator<InfectData>()
                {
                    @Override
                    public int compare(InfectData a, InfectData b)
                    {
                        return b.confirmed.lastElement().compareTo(a.confirmed.lastElement());
                    }
                });
                synchronized (this)
                {
                    infectData = resInfect;
                }
                break;
            case NEWSDATA:
                Vector<NewsData> resNews = new Vector<>();
                JSONArray contents = newsJSON.getJSONArray("datas");
                for (int i = 0; i < contents.size(); ++i)
                {
                    JSONObject content = contents.getJSONObject(i);
                    NewsData data = new NewsData();
                    if (content.getString("type").equals("paper"))
                        data.type = NewsData.NewsType.PAPER;
                    else if (content.getString("type").equals("news"))
                        data.type = NewsData.NewsType.NEWS;
                    else if (content.getString("type").equals("event"))
                        data.type = NewsData.NewsType.EVENT;
                    else data.type = NewsData.NewsType.POINTS;
                    data.id = content.getString("_id");
                    data.time = content.getString("time");
                    data.title = content.getString("title");
                    resNews.add(data);
                }
                Collections.sort(resNews, new Comparator<NewsData>()
                {
                    @Override
                    public int compare(NewsData a, NewsData b)
                    {
                        if (b.time.equals(a.time)) return b.id.compareTo(a.id);
                        return b.time.compareTo(a.time);
                    }
                });
                synchronized (this)
                {
                    newsData = resNews;
                    dictNeedUpdate = true;
                }
                break;
        }
    }

    public String updateCovidData(InfoType type) throws Exception
    {
        String jsonString;
        switch (type)
        {
            case INFECTDATA:
                jsonString = getJSON("https://covid-dashboard.aminer.cn/api/dist/epidemic.json");
                if (jsonString != null)
                {
                    infectJSON = JSONObject.parseObject(jsonString);
                    loadData(type);
                }
                return jsonString;
            case NEWSDATA:
                jsonString = getJSON("https://covid-dashboard.aminer.cn/api/dist/events.json");
                if (jsonString != null)
                {
                    newsJSON = JSONObject.parseObject(jsonString);
                    loadData(type);
                }
                return jsonString;
            default:
        }
        return null;
    }

    public synchronized Vector<InfectData> getInfectData()
    {
        return infectData;
    }

    public synchronized Vector<NewsData> getNewsData(NewsData.NewsType type)
    {
        int i = 0;
        Vector<NewsData> res = new Vector<>();
        String[] labelStrings = KMeans.getInstance().getKmeansLabel().split(" ");
        for (NewsData entry : newsData)
        {
            if (entry.type == type)
            {
                if (type == NewsData.NewsType.EVENT)
                    entry.eventLabel = Integer.parseInt(labelStrings[i]);
                res.add(entry);
                i++;
            }
        }
        return res;
    }

    public Vector<KnowledgeData> getKnowledge(String key) throws Exception
    {
        String jsonString = getJSON("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=" + key);
        if (jsonString == null) return null;
        JSONArray knowledgeContents = JSONObject.parseObject(jsonString).getJSONArray("data");
        Vector<KnowledgeData> res = new Vector<>();
        for (int i = 0; i < knowledgeContents.size(); ++i)
        {
            JSONObject knowledgeContent = knowledgeContents.getJSONObject(i);
            KnowledgeData data = new KnowledgeData();
            data.hot = knowledgeContent.getDouble("hot");
            data.label = knowledgeContent.getString("label");
            data.url = knowledgeContent.getString("url");
            data.imgUrl = knowledgeContent.getString("img");
            JSONObject abstractInfo = knowledgeContent.getJSONObject("abstractInfo");
            if (!abstractInfo.getString("enwiki").equals(""))
                data.description = abstractInfo.getString("enwiki");
            else if (!abstractInfo.getString("baidu").equals(""))
                data.description = abstractInfo.getString("baidu");
            else data.description = abstractInfo.getString("zhwiki");
            JSONObject properties = abstractInfo.getJSONObject("COVID").getJSONObject("properties");
            for (String propertyName : properties.keySet())
                data.properties.put(propertyName, properties.getString(propertyName));
            JSONArray relations = abstractInfo.getJSONObject("COVID").getJSONArray("relations");
            for (int j = 0; j < relations.size(); ++j)
            {
                JSONObject relation = relations.getJSONObject(j);
                if (relation.getBooleanValue("forward"))
                    data.forwardRelations.put(relation.getString("label"), relation.getString("relation"));
                else
                    data.backwardRelations.put(relation.getString("label"), relation.getString("relation"));
            }
            res.add(data);
        }
        Collections.sort(res, new Comparator<KnowledgeData>()
        {
            @Override
            public int compare(KnowledgeData a, KnowledgeData b)
            {
                return b.hot.compareTo(a.hot);
            }
        });
        return res;
    }

    public NewsContent getNewsContent(String id) throws Exception
    {
        String jsonString = getJSON("https://covid-dashboard.aminer.cn/api/event/" + id);
        if (jsonString == null) return null;
        JSONObject newsContent = JSONObject.parseObject(jsonString).getJSONObject("data");
        NewsContent data = new NewsContent();
        data.content = newsContent.getString("content");
        data.date = newsContent.getString("date");
        data.type = newsContent.getString("type");
        if (data.type.equals("news") || data.type.equals("paper"))
            data.source = newsContent.getString("source");
        JSONArray labels = newsContent.getJSONArray("entities");
        for (int i = 0; i < labels.size(); ++i)
            data.labels.add(labels.getJSONObject(i).getString("label"));
        return data;
    }

    public synchronized Vector<EventsVector> getEventsList() throws Exception
    {
        String jsonString = getJSON("https://covid-dashboard.aminer.cn/api/events/list?type=event&page=1&size=1000");
        if (jsonString == null) return null;
        Vector<EventsVector> res = new Vector<>();
        HashSet<String> wordSet = new HashSet<>();
        HashMap<String, Integer> wordCount = new HashMap<>();
        JSONArray eventList = JSONObject.parseObject(jsonString).getJSONArray("data");
        for (int i = 0; i < eventList.size(); ++i)
        {
            JSONObject eventData = eventList.getJSONObject(i);
            String[] words = eventData.getString("seg_text").split(" ");
            HashSet<String> temp = new HashSet<>();
            for (String word : words)
                if (word.length() > 1) temp.add(word);
            wordSet.addAll(temp);
            for (String word : temp)
            {
                if (wordCount.get(word) == null) wordCount.put(word, 1);
                else wordCount.put(word, wordCount.get(word) + 1);
            }
        }

        Vector<String> wordList = new Vector<>(wordSet);
        System.out.println(wordList.size());
        for (String word: wordList)
            System.out.println(word + " ");
        for (int i = 0; i < eventList.size(); ++i)
        {
            EventsVector data = new EventsVector();
            NewsData news = new NewsData();
            JSONObject eventData = eventList.getJSONObject(i);
            news.id = eventData.getString("_id");
            news.time = eventData.getString("time");
            news.title = eventData.getString("title");
            news.type = NewsData.NewsType.EVENT;

            data.news = news;
            data.weight = new Vector<>();

            for (String word: wordList)
            {
                int tf = 0;
                String[] segText = eventData.getString("seg_text").split(" ");
                for (String text: segText)
                    if (text.equals(word)) tf++;
                if (tf == 0) data.weight.add(0.0);
                else
                {
                    double idf = Math.log(eventList.size() / (double) (wordCount.get(word) + 1));
                    data.weight.add(tf * idf);
                }
            }
            res.add(data);
        }
        System.out.println("OK2");
        return res;
    }

    public String[] getEventKeyword(int i)
    {
        String[] res0 = {"卫生", "公共", "工程院", "研究"};
        String[] res1 = {"SARS-CoV-2", "病毒", "细胞", "感染"};
        String[] res2 = {"临床", "试验", "疫苗", "制药"};
        String[] res3 = {"冠状病毒", "蛋白", "蝙蝠", "穿山甲"};
        String[] res4 = {"大学", "研究人员", "新冠", "检测"};
        String[] res5 = {"COVID-19", "患者", "重症", "肺炎"};
        switch (i)
        {
            case 1: return res0;
            case 2: return res1;
            case 3: return res2;
            case 4: return res3;
            case 5: return res4;
            case 6: return res5;
        }
        return res0;
    }

    public synchronized void loadScholarData() throws Exception
    {
        String jsonString = getJSON("https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2");
        if (jsonString == null) return;
        scholarData = new Vector<>();
        JSONArray scholarList = JSONObject.parseObject(jsonString).getJSONArray("data");
        for (int i = 0; i < scholarList.size(); ++i)
        {
            JSONObject scholar = scholarList.getJSONObject(i);
            ScholarData data = new ScholarData();
            data.imgUrl = scholar.getString("avatar");
            data.id = scholar.getString("id");
            JSONObject indices = scholar.getJSONObject("indices");
            data.activity = String.valueOf(indices.getDouble("activity"));
            data.citations = String.valueOf(indices.getInteger("citations"));
            data.diversity = String.valueOf(indices.getDouble("diversity"));
            data.gIndex = String.valueOf(indices.getInteger("gindex"));
            data.hIndex = String.valueOf(indices.getInteger("hindex"));
            data.newStar = String.valueOf(indices.getDouble("newStar"));
            data.risingStar = String.valueOf(indices.getDouble("risingStar"));
            data.pubs = String.valueOf(indices.getInteger("pubs"));
            data.sociability = String.valueOf(indices.getDouble("sociability"));
            data.nameEn = scholar.getString("name");
            data.nameCn = scholar.getString("name_zh");
            data.numFollowed = String.valueOf(scholar.getInteger("num_followed"));
            data.numViewed = String.valueOf(scholar.getInteger("num_viewed"));

            if (scholar.getBoolean("is_passedaway")) data.type = ScholarData.ScholarType.PASSAWAY;
            else data.type = ScholarData.ScholarType.HIGHATTENTION;

            data.detail = new ScholarDataDetailed();
            JSONObject profile = scholar.getJSONObject("profile");
            data.detail.address = profile.getString("address");
            data.detail.affiliation = profile.getString("affiliation");
            data.detail.biography = profile.getString("bio");
            data.detail.education = profile.getString("edu");
            data.detail.email = profile.getString("email");
            data.detail.homepage = profile.getString("homepage");
            data.detail.phone = profile.getString("phone");
            data.detail.position = profile.getString("position");
            data.detail.work = profile.getString("work");

            JSONArray tagName = scholar.getJSONArray("tags");
            JSONArray tagScore = scholar.getJSONArray("tags_score");
            if (tagName != null)
            {
                double totalScore = 0.0;
                for (int j = 0; j < tagName.size(); ++j)
                    totalScore += (double)tagScore.getInteger(j);
                for (int j = 0; j < tagName.size(); ++j)
                {
                    String name = tagName.getString(j);
                    double score = (double)tagScore.getInteger(j);
                    data.detail.tag.put(name, score / totalScore);
                }
            }

            if (data.type == ScholarData.ScholarType.PASSAWAY)
            {
                data.pDetail = new PassawayDataDetailed();
                data.pDetail.passawayDay = String.valueOf(profile.getInteger("passaway_day"));
                data.pDetail.passawayMonth = String.valueOf(profile.getInteger("passaway_month"));
                data.pDetail.passawayYear = String.valueOf(profile.getInteger("passaway_year"));
                data.pDetail.passawayReason = profile.getString("passaway_reason");
            }

            scholarData.add(data);
        }
    }

    public synchronized Vector<ScholarData> getScholarData(ScholarData.ScholarType type)
    {
        Vector<ScholarData> res = new Vector<>();
        for (ScholarData data: scholarData)
            if (data.type == type) res.add(data);
        return res;
    }

    public synchronized Vector<NewsData> searchNewsData(NewsData.NewsType type, String text)
    {
        HashSet<NewsData> temp = new HashSet<>();
        Vector<NewsData> res = new Vector<>();
        List<String> words = segmenter.sentenceProcess(text);
        for (int i = 0; i < text.length(); ++i)
            words.add(String.valueOf(text.charAt(i)));
        for (String word: words)
        {
            if (newsDict.getUselessText().contains(word)) continue;
            HashSet<NewsData> newsList = newsDict.getKeywordDictionary().get(word);
            if (newsList == null) continue;
            for (NewsData news: newsList)
            {
                String title = news.title;
                int tf = (title.length() - title.replace(word, "").length()) / word.length();
                double idf = Math.log(newsDict.getNewsNum() / (double)(newsList.size()));
                news.value += tf * idf;
                temp.add(news);
            }
        }
        for (NewsData news: temp)
            if (news.value != 0 && news.type == type) res.add(news);

        Collections.sort(res, new Comparator<NewsData>()
        {
            @Override
            public int compare(NewsData a, NewsData b)
            {
                if (b.value > a.value + 0.0005) return 1;
                else if (a.value > b.value + 0.0005) return -1;
                else return 0;
            }
        });

        for (NewsData news: temp)
            news.value = 0;

        return res;
    }
}

