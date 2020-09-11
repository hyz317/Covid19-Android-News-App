package com.java.heyuze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

// Kmeans聚类算法
// 调用 public Vector<Vector<EventsVector>> run() 即可

public class KMeans
{
    private int numOfCluster;// 分成多少簇
    private int timeOfIteration;// 迭代次数
    private int dataSetLength;// 数据集元素个数，即数据集的长度
    private Vector<EventsVector> dataSet;// 数据集Vector
    private Vector<EventsVector> center;// 中心Vector
    private Vector<Vector<EventsVector>> cluster; //簇
    private Vector<Double> sumOfErrorSquare;// 误差平方和
    private Random random;
    // private HashMap<String, WordVector> hashMap = new HashMap<>(); // 关键词到词向量的映射

    private KMeans() {}

    private static KMeans instance;

    public static KMeans getInstance()
    {
        if (instance == null) instance = new KMeans();
        return instance;
    }

    // 返回预分类标签
    public String getKmeansLabel()
    {
        return "3 4 4 2 0 4 5 4 4 0 1 3 0 0 4 4 0 0 1 3 0 4 4 2 5 4 4 4 4 1 4 4 2 4 0 4 5" +
                " 5 4 1 1 2 0 1 4 5 5 1 5 1 1 5 1 1 0 5 1 4 0 4 4 4 4 4 5 4 4 3 5 4 2 0 4 2" +
                " 1 4 1 1 2 4 2 1 4 5 0 4 4 1 1 0 0 2 4 5 1 5 4 4 4 3 2 5 2 0 0 4 3 0 0 4 5" +
                " 5 0 4 4 4 3 4 0 0 4 1 4 0 4 5 3 4 5 1 2 1 0 4 0 1 0 5 0 0 4 4 5 2 4 0 2 5" +
                " 1 5 4 4 5 5 2 0 4 4 4 0 3 1 2 4 4 2 3 5 1 1 0 4 0 3 0 4 0 5 4 1 0 0 4 2 0" +
                " 0 3 3 0 4 1 5 5 4 2 0 5 0 4 3 4 4 4 5 4 1 1 1 4 5 5 4 4 2 4 4 4 0 5 1 0 5" +
                " 4 4 1 1 2 1 5 0 0 0 0 5 5 3 0 5 5 2 4 5 5 4 0 0 0 4 4 0 3 3 5 3 0 0 2 5 0" +
                " 0 1 4 2 2 4 4 4 2 2 5 1 5 4 2 4 3 1 4 3 0 0 4 4 1 5 2 2 5 3 4 0 3 0 0 4 0" +
                " 4 4 4 5 2 4 4 1 4 5 4 0 4 5 0 0 1 2 0 3 5 0 3 0 0 1 4 5 1 4 1 4 4 3 1 1 0" +
                " 4 4 4 4 4 0 0 4 0 2 5 4 0 3 2 3 4 0 3 3 4 4 4 0 5 0 2 4 4 5 4 5 0 3 1 4 2" +
                " 0 4 2 4 3 5 0 2 0 0 0 3 0 0 3 4 5 0 5 0 0 0 0 4 0 4 0 0 0 4 4 4 3 0 0 2 5" +
                " 0 0 0 0 4 0 4 1 0 1 1 0 5 1 0 5 1 1 2 5 5 5 1 4 2 2 3 5 4 0 5 3 4 2 1 5 0" +
                " 3 3 3 0 0 3 3 3 3 0 3 3 0 0 3 0 3 4 3 0 3 3 3 3 2 3 3 2 4 3 3 3 3 3 0 0 4" +
                " 4 0 4 2 0 3 0 0 4 3 2 3 3 0 4 3 0 2 3 3 3 4 4 3 0 2 3 2 0 4 3 3 2 3 0 4 4" +
                " 3 2 2 2 3 0 0 2 5 0 4 3 4 5 4 0 5 3 0 3 4 4 0 4 0 1 4 1 0 5 3 2 0 5 0 3 5" +
                " 2 5 4 4 4 0 4 3 4 4 4 0 3 0 4 3 3 1 2 0 0 0 0 0 0 4 0 0 4 3 1 2 0 4 0 5 3" +
                " 5 2 1 0 0 4 1 1 0 2 0 0 0 3 0 5 0 3 5 5 0 2 4 4 0 0 0 1 3 1 4 5 0 1 2 5 3" +
                " 4 4 4 0 5 4 5 4 0 3 0 3 3 5 5 3 1 2 4 5 4 3 4 3 2 0 4 4 0 5 5 1 1 3 0 2 0" +
                " 4 2 0 0 0 0 0 5 0 4 0 0 0 0 0 4 4 3 0 0 0 4 0 0 1 5 5 4 0 2 4 4 4";
    }

    // 设置需分组的原始数据集
    public void setDataSet(Vector<EventsVector> dataSet)
    {
        this.dataSet = dataSet;
    }

    // 获取结果分组
    public Vector<Vector<EventsVector>> getCluster()
    {
        return cluster;
    }

    // 初始化
    private void init()
    {
        timeOfIteration = 0;
        numOfCluster = 6;
        random = new Random();
        dataSetLength = dataSet.size();
        //若numOfCluster大于数据源的长度时，置为数据源的长度
        if (numOfCluster > dataSetLength)
            numOfCluster = dataSetLength;
        center = initCenters();
        cluster = initCluster();
        sumOfErrorSquare = new Vector<Double>();
    }

    // 初始化中心数据Vector，分成多少簇就有多少个中心点
    private Vector<EventsVector> initCenters()
    {
        Vector<EventsVector> center = new Vector<EventsVector>();
        int[] randoms = new int[numOfCluster];
        boolean flag;
        int temp = random.nextInt(dataSetLength);
        randoms[0] = temp;
        //randoms数组中存放数据集的不同的下标
        for (int i = 1; i < numOfCluster; i++)
        {
            flag = true;
            while (flag)
            {
                temp = random.nextInt(dataSetLength);

                int j = 0;
                for (j = 0; j < i; j++)
                {
                    if (randoms[j] == temp)
                    {
                        break;
                    }
                }

                if (j == i)
                {
                    flag = false;
                }
            }
            randoms[i] = temp;
        }

        //测试随机数生成情况
        for (int i = 0; i < numOfCluster; i++)
        {
            System.out.println("test1:randoms[" + i + "]=" + randoms[i]);
        }

        System.out.println();

        for (int i = 0; i < numOfCluster; i++)
            center.add(dataSet.get(randoms[i]));// 生成初始化中心Vector
        return center;
    }

    /**
     * 初始化簇集合
     *
     * @return 一个分为k簇的空数据的簇集合
     */
    private Vector<Vector<EventsVector>> initCluster()
    {
        Vector<Vector<EventsVector>> cluster = new Vector<Vector<EventsVector>>();
        for (int i = 0; i < numOfCluster; i++)
        {
            cluster.add(new Vector<EventsVector>());
        }

        return cluster;
    }

    // 获取距离集合中最小距离的位置
    private int minDistance(double[] distance)
    {
        double minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++)
        {
            if (distance[i] <= minDistance)
            {
                minDistance = distance[i];
                minLocation = i;
            }
        }
        return minLocation;
    }

    /**
     * 核心，将当前元素放到最小距离中心相关的簇中
     */
    private void clusterSet()
    {
        double[] distance = new double[numOfCluster];
        for (int i = 0; i < dataSetLength; i++)
        {
            for (int j = 0; j < numOfCluster; j++)
            {
                distance[j] = dataSet.get(i).getDistance(center.get(j));
                // System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);
            }
            int minLocation = minDistance(distance);
            // System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
            // System.out.println();

            cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

        }
    }

    //计算误差平方和准则函数方法
    private void countRule()
    {
        double jcF = 0;
        for (int i = 0; i < cluster.size(); i++)
            for (int j = 0; j < cluster.get(i).size(); j++)
                jcF += cluster.get(i).get(j).getDistanceSquare(center.get(i));
        sumOfErrorSquare.add(jcF);
    }

    // 设置新的簇中心方法

    private void setNewCenter()
    {
        for (int i = 0; i < numOfCluster; i++)
        {
            int n = cluster.get(i).size();
            if (n != 0)
            {
                EventsVector newCenter = new EventsVector();
                for (int j = 0; j < n; j++)
                {
                    Vector<Double> nowWeight = cluster.get(i).get(j).weight;
                    if (j == 0)
                    {
                        newCenter.weight.setSize(nowWeight.size());
                        for (int k = 0; k < newCenter.weight.size(); ++k)
                            newCenter.weight.set(k, 0.0);
                    }
                    for (int k = 0; k < nowWeight.size(); ++k)
                        newCenter.weight.set(k, newCenter.weight.get(k) + nowWeight.get(k));
                }
                for (int k = 0; k < newCenter.weight.size(); ++k)
                    newCenter.weight.set(k, newCenter.weight.get(k) / n);
                center.set(i, newCenter);
            }
        }
    }

    // Kmeans算法核心过程方法
    private void kmeans()
    {
        init();
        // printDataArray(dataSet,"initDataSet");
        // printDataArray(center,"initCenter");

        // 循环分组，直到误差不变为止
        while (true)
        {
            clusterSet();
            countRule();

            System.out.println("count:" + "sumOfErrorSquare[" + timeOfIteration + "]=" + sumOfErrorSquare.get(timeOfIteration));
            System.out.println();

            // 误差不变了，分组完成
            if (timeOfIteration != 0)
            {
                if (sumOfErrorSquare.get(timeOfIteration) - sumOfErrorSquare.get(timeOfIteration - 1) == 0)
                {
                    break;
                }
            }

            setNewCenter();
            // printDataArray(center,"newCenter");
            timeOfIteration++;
            cluster.clear();
            cluster = initCluster();
        }

        // System.out.println("note:the times of repeat:timeOfIteration="+timeOfIteration);//输出迭代次数
    }

    /*
    public void build(InputStream stream) throws IOException
    {
        InputStreamReader inputReader = new InputStreamReader(stream);
        BufferedReader bufReader = new BufferedReader(inputReader);
        String line = "";
        while ((line = bufReader.readLine()) != null)
        {
            String[] token = line.split(" ");
            WordVector vec = new WordVector();
            vec.wordName = token[0];
            for (int i = 1; i <= 64; ++i)
                vec.wordVector[i - 1] = double.valueOf(token[i]);
            hashMap.put(vec.wordName, vec);
        }
    }
    */

    // 主算法入口
    public Vector<Vector<EventsVector>> run()
    {
        System.out.println("Enter");
        long startTime = System.currentTimeMillis();
        try
        {
            setDataSet(InfoManager.getInstance().getEventsList());
        } catch (Exception e) {e.printStackTrace();}


        System.out.println("Kmeans begins !!!");
        kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("Kmeans running time=" + (endTime - startTime)
                + "ms");
        System.out.println("Kmeans ends");
        System.out.println(getCluster().size());
        for (Vector<EventsVector> test : getCluster())
        {
            System.out.println(test.size());
            for (EventsVector content : test)
                System.out.println(content.news.title);
        }
        return getCluster();
    }
}