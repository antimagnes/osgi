package hu.frigo;

import org.apache.camel.Exchange;
import org.apache.camel.api.management.PerformanceCounter;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        new PerformanceCounter() {
            @Override
            public void completedExchange(Exchange exchange, long l) {

            }

            @Override
            public void failedExchange(Exchange exchange) {

            }

            @Override
            public boolean isStatisticsEnabled() {
                return false;
            }

            @Override
            public void setStatisticsEnabled(boolean b) {

            }
        }.toString();
    }
}
