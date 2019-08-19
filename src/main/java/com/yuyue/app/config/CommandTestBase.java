package com.yuyue.app.config;
import com.yuyue.app.api.domain.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.tobato.fastdfs.conn.ConnectionManager;
import com.github.tobato.fastdfs.conn.FdfsConnectionPool;
import com.github.tobato.fastdfs.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.proto.FdfsCommand;
import org.springframework.stereotype.Component;

/**
 * command测试基类
 *
 * @author lucifer
 *
 */
@Component
public class CommandTestBase {
    /** 日志 */
    public static Logger LOGGER = LoggerFactory.getLogger(CommandTestBase.class);

    /**
     * 连接池
     */
    public ConnectionManager manager = createConnectionManager();

    /**
     * 执行Tracker交易命令
     *
     * @param command
     * @return
     */
    public <T> T executeTrackerCmd(FdfsCommand<T> command) {
        return manager.executeFdfsCmd(Variables.address, command);
    }

    /**
     * 执行存储交易命令
     *
     * @param command
     * @return
     */
    public <T> T executeStoreCmd(FdfsCommand<T> command) {
        return manager.executeFdfsCmd(Variables.store_address, command);
    }

    private ConnectionManager createConnectionManager() {
        return new ConnectionManager(createPool());
    }

    private FdfsConnectionPool createPool() {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectTimeout(Variables.connectTimeout);
        factory.setSoTimeout(Variables.soTimeout);
        return new FdfsConnectionPool(new PooledConnectionFactory());
    }
}
