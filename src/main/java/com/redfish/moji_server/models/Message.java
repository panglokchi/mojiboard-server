package com.redfish.moji_server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.time.LocalDateTime;

@Entity // This tells Hibernate to make a table out of this class
public class Message {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  private String title;
  private String content;

  private LocalDateTime time;

  @JsonBackReference
  @ManyToOne()  
  private Message parent;

  @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
  @JsonIdentityReference(alwaysAsId=true)
  @JsonManagedReference
  @OneToMany(mappedBy = "parent")
  private Set<Message> children = new HashSet<>();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent () {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public Message getParent() {
    return parent;
  }

  public void setParent(Message parent) {
    this.parent = parent;
  }

  public Set<Message> getChildren() {
    return children;
  }

  public void addChildren(Message child) {
    this.children.add(child);
  }

  @JsonProperty("totalChildrenCount")
  public int getTotalChildrenCount() {
    int count = children.size();
    for (Message child : children) {
        count += child.getTotalChildrenCount(); // Recursively count children's children
    }
    return count;
}
}