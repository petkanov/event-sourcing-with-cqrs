package com.techbank.account.cmd;

import com.techbank.account.cmd.peter.repository.EventStoreRepository;
import com.techbank.account.common.events.EventModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ApplicationTests {
	@Autowired
	private EventStoreRepository eventStoreRepository;

	@Test
	void contextLoads() {
		Object asd = eventStoreRepository.findFirstByAggregateIdentifierOrderByVersionDesc("2d5034e0-e717-4d28-8e80-81f3bba1ea6b");

		List<EventModel> dsa = eventStoreRepository.findByAggregateIdentifierOrderByVersionAsc("2d5034e0-e717-4d28-8e80-81f3bba1ea6b");

		dsa.forEach(System.out::println);

		System.out.println(asd);
	}
}
