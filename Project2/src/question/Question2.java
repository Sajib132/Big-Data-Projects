package question;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Question2 {
	
	public static class MapForColdOrHotDay extends Mapper<LongWritable,Text,Text,FloatWritable>{
		
		public void map(LongWritable key,Text value, Context con)throws IOException, InterruptedException{

			String line = value.toString();
			String date = line.substring(6, 14).replaceAll(" ", "");
			String Max = line.substring(38, 45).replaceAll(" ", "");
			String Min = line.substring(46, 53).replaceAll(" ", "");
			Float Max_Temp = Float.parseFloat(Max);
			Float Min_Temp = Float.parseFloat(Min);
			FloatWritable minTempValue = new FloatWritable(Max_Temp);
			FloatWritable maxTempValue = new FloatWritable(Min_Temp);

			if (Min_Temp < 10) {
				con.write(new Text("Cold Day: " + date), minTempValue);
			}
			if (Max_Temp > 35) {
				con.write(new Text("Hot Day:  " + date), maxTempValue);
			}

		}
	}
	public static class ReduceForColdOrHotDay extends Reducer<Text,FloatWritable,Text,FloatWritable>{
		public void reduce(Text word, Iterable<FloatWritable> values,Context con) throws IOException, InterruptedException{
			float temp = 0;
			for (FloatWritable value : values) {
				temp = value.get();

			}
			con.write(new Text(word), new FloatWritable(temp));
		}

	}
	
	public static void main(String[] args)throws IOException, ClassNotFoundException, InterruptedException{
		Configuration c = new Configuration();
		Job j = Job.getInstance(c,"HC Day");
		j.setJarByClass(Question2.class);
		j.setMapperClass(MapForColdOrHotDay.class);
		j.setReducerClass(ReduceForColdOrHotDay.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(FloatWritable.class);
		FileInputFormat.addInputPath(j,new Path(args[0]));
		FileOutputFormat.setOutputPath(j,new Path(args[1]));
		System.exit(j.waitForCompletion(true)?0:1);
	}
	

}