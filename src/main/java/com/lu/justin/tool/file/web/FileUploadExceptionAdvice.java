package com.lu.justin.tool.file.web;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    //    @ResponseBody
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model, Exception e) {
//        return new ResponseEntity<>("Max Size", HttpStatus.PAYLOAD_TOO_LARGE);
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType(MediaType.TEXT_HTML_VALUE);
//        response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE.value());
//        PrintWriter writer = null;
//        try {
//            writer = response.getWriter();
//            writer.write("{errMsg:\"File Size Too Large\"}");
//            writer.flush();
////            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        e.printStackTrace();
        model.addFlashAttribute("message", "File Size Too Large");
        return "redirect:/file";
    }
}
