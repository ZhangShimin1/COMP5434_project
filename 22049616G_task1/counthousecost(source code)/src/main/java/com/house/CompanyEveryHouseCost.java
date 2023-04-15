package com.house;

import com.house.entity.HouseDetail;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算每套房子的成本
 */
public class CompanyEveryHouseCost {
    /**
     * Mapper阶段
     */
    private static class MyMapper extends Mapper<LongWritable, Text, HouseDetail, NullWritable> {
        HouseDetail houseDetail = new HouseDetail();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (key.get() == 0) {
                return;
            }
            String[] split = value.toString().split(",");
            houseDetail.setResidenceSpace(Integer.valueOf(split[3]));
            houseDetail.setUnitPriceOfResidenceSpace(BigDecimal.valueOf(Double.parseDouble(split[18])));
            houseDetail.setBuildingSpace(Integer.valueOf(split[4]));
            houseDetail.setUnitPriceOfBuildingSpace(BigDecimal.valueOf(Double.parseDouble(split[19])));
            houseDetail.setExchangeRate(BigDecimal.valueOf(Double.parseDouble(split[17])));

            context.write(houseDetail, NullWritable.get());
        }
    }

    /**
     * Reducer阶段
     */
    private static class MyReducer extends Reducer<HouseDetail, NullWritable, HouseDetail, DoubleWritable> {

        @Override
        protected void reduce(HouseDetail k2, Iterable<NullWritable> v2s, Context context) throws IOException, InterruptedException {
            //住宅面积
            int residenceSpace = k2.getResidenceSpace();
            //住宅面积的单价
            BigDecimal unitPriceOfResidenceSpace = k2.getUnitPriceOfResidenceSpace();
            //建筑面积
            int buildingSpace = k2.getBuildingSpace();
            //建筑面积的单价
            BigDecimal unitPriceOfBuildingSpace = k2.getUnitPriceOfBuildingSpace();
            //汇率
            BigDecimal exchangeRate = k2.getExchangeRate();
            //总成本的计算规则是。
            //(住宅面积的单价*住宅面积+建筑面积的单价*建筑
            //空间）*汇率=总成本。
            double cost = ((unitPriceOfResidenceSpace.multiply(BigDecimal.valueOf(residenceSpace)))
                    .add(unitPriceOfBuildingSpace.multiply(BigDecimal.valueOf(buildingSpace)))).multiply(exchangeRate)
                    .setScale(2, RoundingMode.UP).doubleValue();
            context.write(k2,new DoubleWritable(cost));

        }
    }

    public static void main(String[] args) {

        try {
            if (args.length < 2){
                System.exit(0);
            }
            //指定Job需要配置的参数
            Configuration conf = new Configuration();
            conf.set("fs.default.name", "hdfs://192.168.135.131:9000");
            conf.set("hadoop.job.user", "hadoop");
            conf.set("mapreduce.framework.name", "yarn");
            //获取Job实例
            Job job = Job.getInstance(conf);
            FileSystem fileSystem = FileSystem.get(conf);
            fileSystem.delete(new Path(args[1]), true);
            FileInputFormat.setInputPaths(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            job.setJarByClass(CompanyEveryHouseCost.class);

            job.setMapperClass(MyMapper.class);
            job.setMapOutputKeyClass(HouseDetail.class);
            job.setMapOutputValueClass(NullWritable.class);
            job.setReducerClass(MyReducer.class);

            job.setNumReduceTasks(2);
            job.setOutputKeyClass(HouseDetail.class);
            job.setOutputValueClass(DoubleWritable.class);

            //提交Job
            job.waitForCompletion(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
