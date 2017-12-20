package cn.hydralisk.common.exception;

import cn.hydralisk.common.constants.HydraliskSystemConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;

/**
 * hydralisk框架异常
 * 使用该异常，打印的日志只会显示hydralisk框架内部的报错，而不会显示第三方框架的报错
 * 减少错误日志的数量
 * created by yangyebo 2017-12-12 上午10:32
 */
public class HydraliskException extends RuntimeException {

    private static final long serialVersionUID = -4811564165918040174L;

    public HydraliskException(String message) {
        super(message);
    }

    public HydraliskException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see Throwable#printStackTrace(PrintWriter)
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        StackTraceElement[] traceElements = getStackTrace();
        if (ArrayUtils.isEmpty(traceElements)) {
            super.printStackTrace(s);
            return;
        }

        int theOriginalHydraliskClassIndex = locateOriginalHydraliskClassIndex(traceElements);
        if (theOriginalHydraliskClassIndex >= 0) {
            //ArrayUtils.subarray方法内部会保证即使lastIndex + 1 > traceElements.length，也不会发生数组越界
            StackTraceElement[] filteredTraceElements = (StackTraceElement[]) ArrayUtils.subarray(
                    traceElements, 0, theOriginalHydraliskClassIndex + 1);
            setStackTrace(filteredTraceElements);
        }
        super.printStackTrace(s);
    }

    /**
     * 定位最初(如果按照日志从上往下看，应该是最后一次)出现匹配cn.hydralisk.*路径的类的位置
     *
     * @param traceElements
     * @return
     */
    private int locateOriginalHydraliskClassIndex(StackTraceElement[] traceElements) {
        int theOriginalHydraliskClassIndex = -1;
        int elementsLength = traceElements.length;
        for (int currentIndex = 0; currentIndex < elementsLength; currentIndex++) {
            if (isHydraliskProjectClass(traceElements[currentIndex])) {
                theOriginalHydraliskClassIndex = currentIndex;
                continue;
            }
            if (hasFoundOriginalHydraliskClass(theOriginalHydraliskClassIndex)) {
                break;
            }
        }
        return theOriginalHydraliskClassIndex;
    }

    private boolean hasFoundOriginalHydraliskClass(int theOriginalHydraliskClassIndex) {
        return theOriginalHydraliskClassIndex != -1;
    }

    private boolean isHydraliskProjectClass(StackTraceElement traceElement) {
        if (traceElement == null) {
            return false;
        }
        if (StringUtils.isBlank(traceElement.getClassName())) {
            return false;
        }
        return traceElement.getClassName().startsWith(
                HydraliskSystemConstants.HYDRALISK_CLASS_PATH_FIXED_PREFIX);
    }
}
