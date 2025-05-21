package com.infosys.main;

import com.infosys.config.AppConfig;
import com.infosys.controller.BookController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        AnnotationConfigApplicationContext context =new AnnotationConfigApplicationContext(AppConfig.class);
        BookController bookController = context.getBean(BookController.class);
        bookController.run();
    }
}
