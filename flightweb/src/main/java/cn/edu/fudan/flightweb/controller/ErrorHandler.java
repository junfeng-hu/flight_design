package cn.edu.fudan.flightweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by junfeng on 8/7/15.
 */
@ControllerAdvice
public class ErrorHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(ErrorHandler.class);
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MetaResult errorResponse(Exception exception) {
        /*
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
    	exception.printStackTrace(pw);
        logger.info(sw.toString());
        */
        logger.error("request failed", exception);
        MetaResult result = new MetaResult();
        result.setStatus(MetaResult.Status.FAILED);
        result.setMessage(exception.getMessage());
        return result;
    }
}