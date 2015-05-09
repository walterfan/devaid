package com.github.walterfan.devaid.mybatis;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.github.walterfan.devaid.App;
import com.github.walterfan.devaid.domain.Link;
import com.github.walterfan.util.ReflectUtils;

public class MyBatisTest {

	private static Log logger = LogFactory.getLog(App.class);

	public static void main(String[] args) {

		try {

			ApplicationContext ctx = new ClassPathXmlApplicationContext(
					"spring-config.xml");


			LinkService service = (LinkService) ctx.getBean("linkService");

			logger.info("Running App...");

			// ( 1 ) SELECT ALL PARENTS
			System.out.println("( 1 ) selectAllLink()");
			List<Link> parents = service.selectAllLink();
			System.out.println("-> " + parents);
			
			
		} catch (Exception e) {
			logger.error("error: ", e);
		}
	}
}