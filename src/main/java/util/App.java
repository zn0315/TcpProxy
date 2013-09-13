package util;

import static java.lang.System.out;

import java.io.IOException;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.slf4j.*;
import org.slf4j.impl.Log4jLoggerAdapter;

/**
 * tcpproxy <listen port> <dst ip> <dst port> -d level
 *
 */
public class App 
{
   	static Logger logger = LoggerFactory.getLogger(App.class);
   	private Options opts;
   	
   	enum OptionName {
   		TRACELEVEL("tracelevel"), DELAY("delay");
   		OptionName(String name) {this.name = name;}
   		String getName() {return name;}
   		private String name;
   	}
   	public App() {
   		buildOptions();
   	}
    public static void main( String[] args ) throws NumberFormatException, IOException, ParseException
    {
    	App app = new App();
    	app.run(args);
    }
    
    private void run(String[] args) throws ParseException, NumberFormatException, IOException {
    	if (args.length < 3) {
    		usage(); 
    		return ;
    	}

    	CommandLineParser parser = new BasicParser();
    	
    	CommandLine line = parser.parse(opts, args);
    	if (line.hasOption(OptionName.TRACELEVEL.getName())) {
    		changeDebugLevel(line.getOptionValue(OptionName.TRACELEVEL.getName()));
    	}
    		//new TcpProxy(Integer.parseInt(args[0]), args[1], 
    		////		Integer.parseInt(args[2]), Integer.parseInt(args[3])).start();
    	int delay = 0;
    	if (line.hasOption(OptionName.DELAY.getName()))
    		delay = Integer.parseInt(line.getOptionValue(OptionName.DELAY.getName()));
    	new TcpMutiSessionProxy(Integer.parseInt(args[0]), args[1], 
    				Integer.parseInt(args[2]), delay).start();
    }
    
    private void usage() {
    	HelpFormatter formatter = new HelpFormatter();
    	formatter.printHelp( "TcpProxy <listenPort> <dst ip> <dst port> [options...]" , opts);
    }
    
    private Options buildOptions() {
    	opts = new Options();
    	Option logLevel = OptionBuilder.withArgName("level").hasArg().withDescription("log level")
    						.create(OptionName.TRACELEVEL.getName());
    	Option delay = OptionBuilder.withArgName("millisecond").hasArg().withDescription("delay unit is millisecond")
    					.create(OptionName.DELAY.getName());
    	opts.addOption(logLevel);
    	opts.addOption(delay);
    	
    	return(opts);
    }
    
    private void changeDebugLevel(String level) {
    	logger.trace("log level:"+level);
    	org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
    	root.setLevel(Level.toLevel(level));
    }
}
