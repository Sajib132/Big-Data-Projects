package question;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class Question4 {
		
	public static class MapForMaxMin extends Mapper<LongWritable,Text,Text,Text>{
		
		public void map(LongWritable key,Text value, Context con)throws IOException, InterruptedException{
			String val = value.toString();
			String date = val.substring(6, 14).replaceAll(" ", "");
			String Max_Temp = val.substring(38, 45).replaceAll(" ", "");
			String Min_Temp = val.substring(46, 53).replaceAll(" ", "");	
			con.write(new Text(date), new Text(Max_Temp+ ":" + Min_Temp));
		}
}
	
	public static class HCReducer extends Reducer<Text,Text,Text,Text>{

		TreeMap<Text, Text> mapper = new TreeMap<Text, Text>();

		public void reduce(Text text, Iterable<Text> values, Context con)
				throws IOException, InterruptedException {

			for (Text value : values) {
				mapper.put(new Text(text), new Text(value));
			}
		}

		public void cleanup(Context con) throws IOException,
				InterruptedException {
			con.write(new Text("Date"), new Text(
					"Max_Tempe"+ "\t" +"Min_Temp"));

			for (Map.Entry<Text, Text> entry : mapper.entrySet()) {
				String[] store = entry.getValue().toString().split(":");

				con.write(new Text(entry.getKey()), new Text(store[0] + "\t " + store[1]));
			}
		}
	}
	
	public static void main(String[] args)throws IOException, ClassNotFoundException, InterruptedException{
		Configuration c = new Configuration();
		Job j = Job.getInstance(c,"Maximum and Minimum Temparature");
		j.setJarByClass(Question4.class);
		j.setMapperClass(MapForMaxMin.class);
		j.setReducerClass(HCReducer.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(j,new Path(args[0]));
		FileOutputFormat.setOutputPath(j,new Path(args[1]));
		System.exit(j.waitForCompletion(true)?0:1);
	}

}
