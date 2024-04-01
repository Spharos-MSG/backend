package spharos.msg.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;
    private String optionType;
    private String optionName;
    private String optionLevel;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_parent_id")
    private Option parent;
    @OneToMany(mappedBy = "parent")
    private List<Option> child;
}
