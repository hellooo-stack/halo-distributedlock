package site.hellooo.distributedlock.common;

import java.lang.management.ManagementFactory;

public class ProcessUtils {
    public static String getProcessId() {
        String fallbackProcessId = "-9999";
        try {
            String mxBeanName = ManagementFactory.getRuntimeMXBean().getName();
            String[] seperatedMxBeanName = mxBeanName.split("@");

            return seperatedMxBeanName[0];
        } catch (Exception ignored) {

        }

        return fallbackProcessId;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(getProcessId());
    }
}
