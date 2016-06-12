import java.util.Properties;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer082;
import org.apache.flink.streaming.util.serialization.RawSchema;
import org.apache.flink.util.Collector;
import org.slf4j.LoggerFactory;


public class FlinkKafkaPerformanceTest {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FlinkKafkaPerformanceTest.class);

	public static void main(String[] args) throws Exception {
		

		Properties props = new Properties();
		props.put("zookeeper.connect", "ipe-koi04.fzi.de:2181");
		props.put("bootstrap.servers", "ipe-koi04.fzi.de:9092");
		props.put("group.id", "group1");
		props.put("zookeeper.session.timeout.ms", "60000");
		props.put("zookeeper.sync.time.ms", "20000");
		props.put("auto.commit.interval.ms", "10000");
	
		StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();

	   // StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("ipe-koi05.fzi.de", 6123);

	    ParameterTool parameterTool = ParameterTool.fromArgs(args);
	    DataStream<byte[]> dataStream4 = env.addSource(new FlinkKafkaConsumer082<>("SEPA.SEP.Random.Number.Json", new RawSchema(), props)).setParallelism(1);

	    dataStream4.flatMap(new FlatMapFunction<byte[], Integer>() {
	        long received = 0;
	        long logfreq = 10000;
	        long lastLog = -1;
	        long lastElements = 0;

	        @Override
	        public void flatMap(byte[] element, Collector<Integer> collector) throws Exception {
	        	
	            received++;
	            if (received % logfreq == 0) {
	                // throughput over entire time
	                long now = System.currentTimeMillis();

	                // throughput for the last "logfreq" elements
	                if(lastLog == -1) {
	                    // init (the first)
	                    lastLog = now;
	                    lastElements = received;
	                } else {
	                    long timeDiff = now - lastLog;
	                    long elementDiff = received - lastElements;
	                    double ex = (1000/(double)timeDiff);
	                    LOG.info("During the last {} ms, we received {} elements. That's {} elements/second/core. GB received {}",
	                            timeDiff, elementDiff, elementDiff*ex, (received * 2500) / 1024 / 1024 / 1024);
	                    // reinit
	                    lastLog = now;
	                    lastElements = received;
	                }
	            }
	        }
	    });

	    env.execute("Raw kafka throughput");
	}	
}