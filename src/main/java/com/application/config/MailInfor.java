package com.application.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MailInfor {
    private String from = "myappmail";
    private String to;
    private String[] cc; // đính kèm danh sách mail người nhận
    private String[] bcc; // không đính kèm danh sách mail người nhận
    private String title;
    private String body;
    private List<File> list = new ArrayList<>();

    public MailInfor(String title, String to, String body) {
        this.title = title;
        this.to = to;
        this.body = body;
    }
}