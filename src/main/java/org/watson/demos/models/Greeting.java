package org.watson.demos.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"content", "locale"}))
@Setter(AccessLevel.PROTECTED) // For @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For @Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE) // For @Builder
public class Greeting implements Serializable {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, length = 36)
    private UUID id;

    @NotBlank
    @Basic(optional = false)
    private String content;

    @Builder.Default
    private Locale locale = Locale.getDefault();

    @Basic(optional = false)
    @CreatedDate
    private ZonedDateTime created;

    @PrePersist
    protected void onCreate() {
        created = ZonedDateTime.now().withNano(0);
    }
}
