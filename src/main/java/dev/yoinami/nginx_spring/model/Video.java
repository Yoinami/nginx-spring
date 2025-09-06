package dev.yoinami.nginx_spring.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table("video")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Video implements Persistable<String> {
    @Id
    private String id;
    private String title;

    @Transient
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }
}