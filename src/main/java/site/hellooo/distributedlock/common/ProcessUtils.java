package site.hellooo.distributedlock.common;

import java.lang.management.ManagementFactory;

public class ProcessUtils {
    private static final String DEFAULT_FALLBACK_PROCESS_ID = "-9999";
    public static String getProcessId() {

        try {
            String mxBeanName = ManagementFactory.getRuntimeMXBean().getName();
            String[] seperatedMxBeanName = mxBeanName.split("@");

            return seperatedMxBeanName[0];
        } catch (Exception ignored) {

        }

//        if no process id returned, return the default one
        return DEFAULT_FALLBACK_PROCESS_ID;
    }
}
