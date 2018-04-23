package org.my.study.spring.batch.jsr352.listener;

import java.util.List;

import org.my.study.spring.batch.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger LOG = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	private static final String SQL_QUERY = "SELECT first_name, last_name FROM people";

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			LOG.info("!!! JOB FINISHED! Time to verify the results");

			List<Person> results = jdbcTemplate.query(SQL_QUERY, (rs, row) -> {
                return new Person(rs.getString(1), rs.getString(2));
            });

			for (Person person : results) {
				LOG.info("Found <" + person + "> in the database.");
			}

		}
	}
}
