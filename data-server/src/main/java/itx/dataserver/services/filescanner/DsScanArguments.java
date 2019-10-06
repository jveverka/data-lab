package itx.dataserver.services.filescanner;

import com.beust.jcommander.Parameter;

public class DsScanArguments {

    @Parameter(names = {"-e", "--executor-size" }, description = "Number of threads in executor thread pool.")
    private int executorSize = 1;

    @Parameter(names = {"-p", "--root-path" }, description = "Root path of directory to scan.", required = true)
    private String rootPath;

    @Parameter(names = {"-eh", "--elastic-host" }, description = "Host name of ElasticSearch server.")
    private String elasticHost = "127.0.0.1";

    @Parameter(names = {"-ep", "--elastic-port" }, description = "Port number of ElasticSearch server.")
    private int elasticPort = 9200;

    public int getExecutorSize() {
        return executorSize;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getElasticHost() {
        return elasticHost;
    }

    public int getElasticPort() {
        return elasticPort;
    }

}
