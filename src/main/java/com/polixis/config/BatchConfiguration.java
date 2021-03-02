package com.polixis.config;


import com.polixis.entity.User;
import com.polixis.processor.UserItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Value("data/*.csv")
    private Resource[] inputResources;
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Autowired
    public void setStepBuilderFactory(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public MultiResourceItemReader<User> multiResourceItemReader() {
        MultiResourceItemReader<User> resourceItemReader = new MultiResourceItemReader<User>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://localhost/users_db?serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("SPOSOAD-19950901");
        return dataSource;
    }

    @Bean
    public FlatFileItemReader<User> reader() {
        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setLineMapper(new DefaultLineMapper<User>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("firstName", "lastName", "date");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                setTargetType(User.class);
            }});
        }});
        reader.setLinesToSkip(1);

        return reader;
    }

    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<User> writer() {
        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<User>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
        writer.setSql("INSERT INTO users(first_name, last_name, date) VALUES(:firstName, :lastName, :date)");
        writer.setDataSource(dataSource());
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory
                .get("step1")
                .<User, User>chunk(3)
                .reader(multiResourceItemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importUserJob() {
        return jobBuilderFactory
                .get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }
}
