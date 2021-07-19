package material.domain;

import static org.assertj.core.api.Assertions.assertThat;

import material.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UgovorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ugovor.class);
        Ugovor ugovor1 = new Ugovor();
        ugovor1.setId(1L);
        Ugovor ugovor2 = new Ugovor();
        ugovor2.setId(ugovor1.getId());
        assertThat(ugovor1).isEqualTo(ugovor2);
        ugovor2.setId(2L);
        assertThat(ugovor1).isNotEqualTo(ugovor2);
        ugovor1.setId(null);
        assertThat(ugovor1).isNotEqualTo(ugovor2);
    }
}
