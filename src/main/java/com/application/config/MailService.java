package com.application.config;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MailService {
    @Autowired
    private JavaMailSender sender;
    @Autowired
    SpringTemplateEngine springTemplateEngine;
    List<WrapMailAndThymeleaf> list = new ArrayList<>();
    public void send(MailInfor mailInfor,RegisterForThymeleaf register) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        // thiết lập thông tin cần thiết cho mail (message,cho phép đính file,encoding)
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        Context context = new Context();
//        RegisterForThymeleaf register = (RegisterForThymeleaf) objects[0];
        context.setVariables(register.getVariable());
        helper.setFrom(mailInfor.getFrom());
        helper.setTo(mailInfor.getTo());
        helper.setSubject(mailInfor.getTitle());
        helper.setReplyTo(mailInfor.getFrom());
        String html = springTemplateEngine.process(register.getPath(),context);
        helper.setText(html,true);
        String cc[] = mailInfor.getCc();
        if(cc != null && cc.length >0) {
            helper.setCc(cc);
        }
        String bcc[] = mailInfor.getBcc();
        if(bcc != null && bcc.length >0) {
            helper.setBcc(bcc);
        }
        List<File> list = mailInfor.getList();
        if(list.size()>0) {
            for (File file : list) {
                helper.addAttachment(file.getName(), file);
            }
        }
        sender.send(message);
    }

    public void send(String to, String title, String body,RegisterForThymeleaf register) throws MessagingException {
        this.send(new MailInfor(title, to, body),register);
    }
    public void queue(MailInfor mailInfor,RegisterForThymeleaf register){
        this.list.add(new WrapMailAndThymeleaf(mailInfor,register));
    }
//    public void queue(MailInfor mailInfor,BillForThymeleaf bill){
//        this.listBill.add(new WrapMailBillAndThymeleaf(mailInfor,bill));
//    }
    public void queue(String to, String title, String body,RegisterForThymeleaf register) throws MessagingException {
        log.info("Send mail ne");
        this.queue(new MailInfor(title, to, body),register);
    }
    @Scheduled(fixedDelay = 3000)
    public void run(){
        while (!list.isEmpty()){
            WrapMailAndThymeleaf wrapMailAndThymeleaf = list.remove(0);
            try {
                this.send(wrapMailAndThymeleaf.getMailInfor(),wrapMailAndThymeleaf.getRegister());
                log.info(String.format("send mail to %s success",wrapMailAndThymeleaf.getMailInfor().getTo()));
            }
            catch (Exception e){
                log.info(String.format("send mail to %s fail",wrapMailAndThymeleaf.getMailInfor().getTo()));
            }
        }
    }
}
