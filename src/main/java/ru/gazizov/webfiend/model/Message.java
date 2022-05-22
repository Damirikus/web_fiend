package ru.gazizov.webfiend.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime date;
    private String tag;
    @NotBlank(message = "Please enter the message!")
    @Length(max = 2048, message = "Message too long!")
    private String text;

    //добавляем автора, кто написал
    @ManyToOne(fetch = FetchType.EAGER) //означает что один автор может написать много сообщений
    @JoinColumn(name = "usr_id") //поменяли название в таблице
    private User author;

    private String filename;


    public Message(String tag, String text, User user) {
        this.date = LocalDateTime.now();
        this.tag = tag;
        this.text = text;
        this.author = user;
    }

    public String getAuthorName(){
        return author != null ? author.getUsername() : "<none>";
    }

    public Message() {
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return getId() == message.getId() && Objects.equals(getDate(), message.getDate()) && Objects.equals(getTag(), message.getTag()) && Objects.equals(getText(), message.getText()) && Objects.equals(getAuthor(), message.getAuthor()) && Objects.equals(getFilename(), message.getFilename());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDate(), getTag(), getText(), getAuthor(), getFilename());
    }
}
