package com.java.heyuze.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.heyuze.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class CovidInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covidinfo);

        Intent intent = this.getIntent();
        String name = intent.getStringExtra("Name");
        Vector<Integer> confirmed = String2Vector(Objects.requireNonNull(intent.getStringExtra("Confirmed")));
        Vector<Integer> cured = String2Vector(Objects.requireNonNull(intent.getStringExtra("Cured")));
        Vector<Integer> dead = String2Vector(Objects.requireNonNull(intent.getStringExtra("Dead")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        try {
            beginDate = sdf.parse(Objects.requireNonNull(intent.getStringExtra("BeginDate")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LineChartView chart = findViewById(R.id.chart);
        List<PointValue> values_1 = new ArrayList<PointValue>();
        List<PointValue> values_2 = new ArrayList<PointValue>();
        List<PointValue> values_3 = new ArrayList<PointValue>();
        List<AxisValue> mAxisValues = new ArrayList<AxisValue>();

        for (int i = 0; i < confirmed.size(); i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(beginDate);
            calendar.add(Calendar.DATE, i);
            Date newDate = calendar.getTime();
            String strDate = sdf.format(newDate);
            values_1.add(new PointValue(i, confirmed.get(i)));
            values_2.add(new PointValue(i, cured.get(i)));
            values_3.add(new PointValue(i, dead.get(i)));
            mAxisValues.add(new AxisValue(i).setLabel(strDate));
            // System.out.println(strDate);
        }
        Line line_1 = new Line(values_1).setColor(Color.argb(200, 252, 175, 139)).setCubic(true);
        Line line_2 = new Line(values_2).setColor(Color.argb(200, 174, 243, 148)).setCubic(true);
        Line line_3 = new Line(values_3).setColor(Color.argb(200, 125, 125, 125)).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line_1);
        lines.add(line_2);
        lines.add(line_3);
        line_1.setPointRadius(1);
        line_2.setPointRadius(1);
        line_3.setPointRadius(1);
        LineChartData data = new LineChartData(lines);
        // 坐标轴
        Axis axisX = new Axis(); // X轴
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("日期");
        axisX.setMaxLabelChars(10);
        axisX.setValues(mAxisValues);
        data.setAxisXBottom(axisX);

        Axis axisY = new Axis();  // Y轴
        axisY.setName("人数");
        axisY.setMaxLabelChars(7); // default = 3
        axisY.setTextColor(Color.BLACK);
        data.setAxisYLeft(axisY);

        chart.setInteractive(true);
        chart.setZoomType(ZoomType.HORIZONTAL);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chart.setLineChartData(data);

        TextView title = findViewById(R.id.covid_info_title);
        String text = name + " 疫情";
        title.setText(text);

        TextView number1 = findViewById(R.id.now_increase);
        TextView number2 = findViewById(R.id.now_confirmed);
        TextView number3 = findViewById(R.id.now_cured);
        TextView number4 = findViewById(R.id.now_dead);

        number1.setText(String.valueOf(confirmed.get(confirmed.size() - 1) - confirmed.get(confirmed.size() - 2)));
        number2.setText(String.valueOf(confirmed.get(confirmed.size() - 1)));
        number3.setText(String.valueOf(cured.get(cured.size() - 1)));
        number4.setText(String.valueOf(dead.get(dead.size() - 1)));
    }


    private Vector<Integer> String2Vector(String str) {
        String[] arr = str.split(",");
        Vector<Integer> v = new Vector<>();
        for (int i = 0; i < arr.length; i++) {
            v.add(Integer.parseInt(arr[i]));
        }
        return v;
    }
}
