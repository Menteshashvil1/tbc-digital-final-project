package ge.tbc.testautomation.database;

import ge.tbc.testautomation.utils.ConfigReader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Properties;

public final class MyBatisSessionFactory {
    private static final String CONFIG = "mybatis/mybatis-config.xml";
    private static final List<String> SEED_SCRIPTS = List.of("testdata/schema.sql", "testdata/data.sql");

    private static volatile SqlSessionFactory factory;

    private MyBatisSessionFactory() {
    }

    public static SqlSession openSession() {
        return factory().openSession(true);
    }

    private static SqlSessionFactory factory() {
        if (factory == null) {
            synchronized (MyBatisSessionFactory.class) {
                if (factory == null) {
                    SqlSessionFactory built = build();
                    seed(built);
                    factory = built;
                }
            }
        }
        return factory;
    }

    private static SqlSessionFactory build() {
        Properties properties = new Properties();
        properties.setProperty("db.url", ConfigReader.get("db.url"));
        properties.setProperty("db.username", ConfigReader.get("db.username"));
        properties.setProperty("db.password", ConfigReader.get("db.password"));

        try (Reader reader = Resources.getResourceAsReader(CONFIG)) {
            return new SqlSessionFactoryBuilder().build(reader, properties);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void seed(SqlSessionFactory sessionFactory) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);
            runner.setStopOnError(true);
            for (String script : SEED_SCRIPTS) {
                try (Reader reader = Resources.getResourceAsReader(script)) {
                    runner.runScript(reader);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
