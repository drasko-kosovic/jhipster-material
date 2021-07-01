package material.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Ponude.
 */
@Entity
@Table(name = "ponude")
public class Ponude implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "naziv", nullable = false)
    private String naziv;

    @OneToOne
    @JoinColumn(unique = true)
    private Ponudjaci ponudjaci;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ponude id(Long id) {
        this.id = id;
        return this;
    }

    public String getNaziv() {
        return this.naziv;
    }

    public Ponude naziv(String naziv) {
        this.naziv = naziv;
        return this;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Ponudjaci getPonudjaci() {
        return this.ponudjaci;
    }

    public Ponude ponudjaci(Ponudjaci ponudjaci) {
        this.setPonudjaci(ponudjaci);
        return this;
    }

    public void setPonudjaci(Ponudjaci ponudjaci) {
        this.ponudjaci = ponudjaci;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ponude)) {
            return false;
        }
        return id != null && id.equals(((Ponude) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ponude{" +
            "id=" + getId() +
            ", naziv='" + getNaziv() + "'" +
            "}";
    }
}
