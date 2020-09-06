package pp;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Temp {
//    public static class Mymapper extends Mapper<Object, Text, IntWritable,Text> {
//
//        public void map(Object key, Text value,Context context) throws IOException, InterruptedException{
//
//            int month=Integer.parseInt(value.toString().substring(16, 18));
//            IntWritable mon=new IntWritable(month);
//            String temp=value.toString().substring(26,30);
//            String t=null;
//            for(int i=0;i<temp.length();i++){
//                if(temp.charAt(i)==',')
//                        break;
//                else
//                    t=t+temp.charAt(i);
//            }
//            Text data=new Text(value.toString().substring(21, 25)+t);
//            context.write(mon, data);
//        }
//    }
    
    public static class MapForWordCount extends Mapper<LongWritable,Text,Text,FloatWritable>
	{
		public void map(LongWritable key,Text value, Context con) throws IOException, InterruptedException
		{
			String line= value.toString();
			String[] words=line.split(" ");
//			for(String word:words)
//			{
//				Text outputkey=new Text(word);
//				IntWritable outputvalue=new IntWritable(1);
//				con.write(outputkey,outputvalue);
//			}
			
			String s =words[1];
			Text outputkey = new Text(s.substring(0,4));
			Float nt = Float.parseFloat(words[7]);
			//Float mt = Float.parseFloat(words[8]);
			FloatWritable outputValue = new FloatWritable(nt);
			con.write(outputkey, outputValue);
			
			
			
			
			
			
		}
		
	}

    
    public static class ReduceForWordCount extends Reducer<Text,FloatWritable,Text,FloatWritable>
	{
		public void reduce(Text word,Iterable<FloatWritable> values,Context con)throws IOException, InterruptedException
		{
			Float sum= (float) 0.0;
			for(FloatWritable value:values)
			{
				sum=sum+value.get();
			}
			con.write(word,new FloatWritable(sum));
		}
		
	}

    
    
    

//    public static class Myreducer extends  Reducer<IntWritable,Text,IntWritable,IntWritable> {
//
//        public void reduce(IntWritable key,Iterable<Text> values,Context context) throws IOException, InterruptedException{
//            String temp="";
//            int max=0;
//            for(Text t:values)
//            {
//                temp=t.toString();
//                if(temp.substring(0, 4)=="TMAX"){
//    if(Integer.parseInt(temp.substring(4,temp.length()))>max){
//                        max=Integer.parseInt(temp.substring(4,temp.length()));
//                    }
//                }
//            }
//
//            context.write(key,new IntWritable(max));
//        }
//
//        }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "temp");
        job.setJarByClass(Temp.class);
        job.setMapperClass(MapForWordCount.class);

        job.setReducerClass(ReduceForWordCount.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);

        }
}