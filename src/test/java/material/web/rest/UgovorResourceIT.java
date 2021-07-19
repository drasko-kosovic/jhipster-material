package material.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import material.IntegrationTest;
import material.domain.Ugovor;
import material.repository.UgovorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UgovorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UgovorResourceIT {

    private static final String DEFAULT_BROJ_UGOVORA = "AAAAAAAAAA";
    private static final String UPDATED_BROJ_UGOVORA = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATUM_UGOVORA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATUM_UGOVORA = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_BROJ_ODLUKE = "AAAAAAAAAA";
    private static final String UPDATED_BROJ_ODLUKE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATUM_ODLUKE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATUM_ODLUKE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_IZNOS_UGOVORA_BEZ_PDF = 1D;
    private static final Double UPDATED_IZNOS_UGOVORA_BEZ_PDF = 2D;

    private static final Integer DEFAULT_SIFRA_POSTUPKA = 1;
    private static final Integer UPDATED_SIFRA_POSTUPKA = 2;

    private static final Integer DEFAULT_SIFRA_PONUDE = 1;
    private static final Integer UPDATED_SIFRA_PONUDE = 2;

    private static final Integer DEFAULT_SIFRA_PONUDJACA = 1;
    private static final Integer UPDATED_SIFRA_PONUDJACA = 2;

    private static final String ENTITY_API_URL = "/api/ugovors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UgovorRepository ugovorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUgovorMockMvc;

    private Ugovor ugovor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ugovor createEntity(EntityManager em) {
        Ugovor ugovor = new Ugovor()
            .brojUgovora(DEFAULT_BROJ_UGOVORA)
            .datumUgovora(DEFAULT_DATUM_UGOVORA)
            .brojOdluke(DEFAULT_BROJ_ODLUKE)
            .datumOdluke(DEFAULT_DATUM_ODLUKE)
            .iznosUgovoraBezPdf(DEFAULT_IZNOS_UGOVORA_BEZ_PDF)
            .sifraPostupka(DEFAULT_SIFRA_POSTUPKA)
            .sifraPonude(DEFAULT_SIFRA_PONUDE)
            .sifraPonudjaca(DEFAULT_SIFRA_PONUDJACA);
        return ugovor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ugovor createUpdatedEntity(EntityManager em) {
        Ugovor ugovor = new Ugovor()
            .brojUgovora(UPDATED_BROJ_UGOVORA)
            .datumUgovora(UPDATED_DATUM_UGOVORA)
            .brojOdluke(UPDATED_BROJ_ODLUKE)
            .datumOdluke(UPDATED_DATUM_ODLUKE)
            .iznosUgovoraBezPdf(UPDATED_IZNOS_UGOVORA_BEZ_PDF)
            .sifraPostupka(UPDATED_SIFRA_POSTUPKA)
            .sifraPonude(UPDATED_SIFRA_PONUDE)
            .sifraPonudjaca(UPDATED_SIFRA_PONUDJACA);
        return ugovor;
    }

    @BeforeEach
    public void initTest() {
        ugovor = createEntity(em);
    }

    @Test
    @Transactional
    void createUgovor() throws Exception {
        int databaseSizeBeforeCreate = ugovorRepository.findAll().size();
        // Create the Ugovor
        restUgovorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ugovor)))
            .andExpect(status().isCreated());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeCreate + 1);
        Ugovor testUgovor = ugovorList.get(ugovorList.size() - 1);
        assertThat(testUgovor.getBrojUgovora()).isEqualTo(DEFAULT_BROJ_UGOVORA);
        assertThat(testUgovor.getDatumUgovora()).isEqualTo(DEFAULT_DATUM_UGOVORA);
        assertThat(testUgovor.getBrojOdluke()).isEqualTo(DEFAULT_BROJ_ODLUKE);
        assertThat(testUgovor.getDatumOdluke()).isEqualTo(DEFAULT_DATUM_ODLUKE);
        assertThat(testUgovor.getIznosUgovoraBezPdf()).isEqualTo(DEFAULT_IZNOS_UGOVORA_BEZ_PDF);
        assertThat(testUgovor.getSifraPostupka()).isEqualTo(DEFAULT_SIFRA_POSTUPKA);
        assertThat(testUgovor.getSifraPonude()).isEqualTo(DEFAULT_SIFRA_PONUDE);
        assertThat(testUgovor.getSifraPonudjaca()).isEqualTo(DEFAULT_SIFRA_PONUDJACA);
    }

    @Test
    @Transactional
    void createUgovorWithExistingId() throws Exception {
        // Create the Ugovor with an existing ID
        ugovor.setId(1L);

        int databaseSizeBeforeCreate = ugovorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUgovorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ugovor)))
            .andExpect(status().isBadRequest());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUgovors() throws Exception {
        // Initialize the database
        ugovorRepository.saveAndFlush(ugovor);

        // Get all the ugovorList
        restUgovorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ugovor.getId().intValue())))
            .andExpect(jsonPath("$.[*].brojUgovora").value(hasItem(DEFAULT_BROJ_UGOVORA)))
            .andExpect(jsonPath("$.[*].datumUgovora").value(hasItem(DEFAULT_DATUM_UGOVORA.toString())))
            .andExpect(jsonPath("$.[*].brojOdluke").value(hasItem(DEFAULT_BROJ_ODLUKE)))
            .andExpect(jsonPath("$.[*].datumOdluke").value(hasItem(DEFAULT_DATUM_ODLUKE.toString())))
            .andExpect(jsonPath("$.[*].iznosUgovoraBezPdf").value(hasItem(DEFAULT_IZNOS_UGOVORA_BEZ_PDF.doubleValue())))
            .andExpect(jsonPath("$.[*].sifraPostupka").value(hasItem(DEFAULT_SIFRA_POSTUPKA)))
            .andExpect(jsonPath("$.[*].sifraPonude").value(hasItem(DEFAULT_SIFRA_PONUDE)))
            .andExpect(jsonPath("$.[*].sifraPonudjaca").value(hasItem(DEFAULT_SIFRA_PONUDJACA)));
    }

    @Test
    @Transactional
    void getUgovor() throws Exception {
        // Initialize the database
        ugovorRepository.saveAndFlush(ugovor);

        // Get the ugovor
        restUgovorMockMvc
            .perform(get(ENTITY_API_URL_ID, ugovor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ugovor.getId().intValue()))
            .andExpect(jsonPath("$.brojUgovora").value(DEFAULT_BROJ_UGOVORA))
            .andExpect(jsonPath("$.datumUgovora").value(DEFAULT_DATUM_UGOVORA.toString()))
            .andExpect(jsonPath("$.brojOdluke").value(DEFAULT_BROJ_ODLUKE))
            .andExpect(jsonPath("$.datumOdluke").value(DEFAULT_DATUM_ODLUKE.toString()))
            .andExpect(jsonPath("$.iznosUgovoraBezPdf").value(DEFAULT_IZNOS_UGOVORA_BEZ_PDF.doubleValue()))
            .andExpect(jsonPath("$.sifraPostupka").value(DEFAULT_SIFRA_POSTUPKA))
            .andExpect(jsonPath("$.sifraPonude").value(DEFAULT_SIFRA_PONUDE))
            .andExpect(jsonPath("$.sifraPonudjaca").value(DEFAULT_SIFRA_PONUDJACA));
    }

    @Test
    @Transactional
    void getNonExistingUgovor() throws Exception {
        // Get the ugovor
        restUgovorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUgovor() throws Exception {
        // Initialize the database
        ugovorRepository.saveAndFlush(ugovor);

        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();

        // Update the ugovor
        Ugovor updatedUgovor = ugovorRepository.findById(ugovor.getId()).get();
        // Disconnect from session so that the updates on updatedUgovor are not directly saved in db
        em.detach(updatedUgovor);
        updatedUgovor
            .brojUgovora(UPDATED_BROJ_UGOVORA)
            .datumUgovora(UPDATED_DATUM_UGOVORA)
            .brojOdluke(UPDATED_BROJ_ODLUKE)
            .datumOdluke(UPDATED_DATUM_ODLUKE)
            .iznosUgovoraBezPdf(UPDATED_IZNOS_UGOVORA_BEZ_PDF)
            .sifraPostupka(UPDATED_SIFRA_POSTUPKA)
            .sifraPonude(UPDATED_SIFRA_PONUDE)
            .sifraPonudjaca(UPDATED_SIFRA_PONUDJACA);

        restUgovorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUgovor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUgovor))
            )
            .andExpect(status().isOk());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
        Ugovor testUgovor = ugovorList.get(ugovorList.size() - 1);
        assertThat(testUgovor.getBrojUgovora()).isEqualTo(UPDATED_BROJ_UGOVORA);
        assertThat(testUgovor.getDatumUgovora()).isEqualTo(UPDATED_DATUM_UGOVORA);
        assertThat(testUgovor.getBrojOdluke()).isEqualTo(UPDATED_BROJ_ODLUKE);
        assertThat(testUgovor.getDatumOdluke()).isEqualTo(UPDATED_DATUM_ODLUKE);
        assertThat(testUgovor.getIznosUgovoraBezPdf()).isEqualTo(UPDATED_IZNOS_UGOVORA_BEZ_PDF);
        assertThat(testUgovor.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testUgovor.getSifraPonude()).isEqualTo(UPDATED_SIFRA_PONUDE);
        assertThat(testUgovor.getSifraPonudjaca()).isEqualTo(UPDATED_SIFRA_PONUDJACA);
    }

    @Test
    @Transactional
    void putNonExistingUgovor() throws Exception {
        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();
        ugovor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUgovorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ugovor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ugovor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUgovor() throws Exception {
        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();
        ugovor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUgovorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ugovor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUgovor() throws Exception {
        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();
        ugovor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUgovorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ugovor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUgovorWithPatch() throws Exception {
        // Initialize the database
        ugovorRepository.saveAndFlush(ugovor);

        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();

        // Update the ugovor using partial update
        Ugovor partialUpdatedUgovor = new Ugovor();
        partialUpdatedUgovor.setId(ugovor.getId());

        partialUpdatedUgovor
            .brojUgovora(UPDATED_BROJ_UGOVORA)
            .brojOdluke(UPDATED_BROJ_ODLUKE)
            .iznosUgovoraBezPdf(UPDATED_IZNOS_UGOVORA_BEZ_PDF)
            .sifraPostupka(UPDATED_SIFRA_POSTUPKA)
            .sifraPonude(UPDATED_SIFRA_PONUDE)
            .sifraPonudjaca(UPDATED_SIFRA_PONUDJACA);

        restUgovorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUgovor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUgovor))
            )
            .andExpect(status().isOk());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
        Ugovor testUgovor = ugovorList.get(ugovorList.size() - 1);
        assertThat(testUgovor.getBrojUgovora()).isEqualTo(UPDATED_BROJ_UGOVORA);
        assertThat(testUgovor.getDatumUgovora()).isEqualTo(DEFAULT_DATUM_UGOVORA);
        assertThat(testUgovor.getBrojOdluke()).isEqualTo(UPDATED_BROJ_ODLUKE);
        assertThat(testUgovor.getDatumOdluke()).isEqualTo(DEFAULT_DATUM_ODLUKE);
        assertThat(testUgovor.getIznosUgovoraBezPdf()).isEqualTo(UPDATED_IZNOS_UGOVORA_BEZ_PDF);
        assertThat(testUgovor.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testUgovor.getSifraPonude()).isEqualTo(UPDATED_SIFRA_PONUDE);
        assertThat(testUgovor.getSifraPonudjaca()).isEqualTo(UPDATED_SIFRA_PONUDJACA);
    }

    @Test
    @Transactional
    void fullUpdateUgovorWithPatch() throws Exception {
        // Initialize the database
        ugovorRepository.saveAndFlush(ugovor);

        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();

        // Update the ugovor using partial update
        Ugovor partialUpdatedUgovor = new Ugovor();
        partialUpdatedUgovor.setId(ugovor.getId());

        partialUpdatedUgovor
            .brojUgovora(UPDATED_BROJ_UGOVORA)
            .datumUgovora(UPDATED_DATUM_UGOVORA)
            .brojOdluke(UPDATED_BROJ_ODLUKE)
            .datumOdluke(UPDATED_DATUM_ODLUKE)
            .iznosUgovoraBezPdf(UPDATED_IZNOS_UGOVORA_BEZ_PDF)
            .sifraPostupka(UPDATED_SIFRA_POSTUPKA)
            .sifraPonude(UPDATED_SIFRA_PONUDE)
            .sifraPonudjaca(UPDATED_SIFRA_PONUDJACA);

        restUgovorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUgovor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUgovor))
            )
            .andExpect(status().isOk());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
        Ugovor testUgovor = ugovorList.get(ugovorList.size() - 1);
        assertThat(testUgovor.getBrojUgovora()).isEqualTo(UPDATED_BROJ_UGOVORA);
        assertThat(testUgovor.getDatumUgovora()).isEqualTo(UPDATED_DATUM_UGOVORA);
        assertThat(testUgovor.getBrojOdluke()).isEqualTo(UPDATED_BROJ_ODLUKE);
        assertThat(testUgovor.getDatumOdluke()).isEqualTo(UPDATED_DATUM_ODLUKE);
        assertThat(testUgovor.getIznosUgovoraBezPdf()).isEqualTo(UPDATED_IZNOS_UGOVORA_BEZ_PDF);
        assertThat(testUgovor.getSifraPostupka()).isEqualTo(UPDATED_SIFRA_POSTUPKA);
        assertThat(testUgovor.getSifraPonude()).isEqualTo(UPDATED_SIFRA_PONUDE);
        assertThat(testUgovor.getSifraPonudjaca()).isEqualTo(UPDATED_SIFRA_PONUDJACA);
    }

    @Test
    @Transactional
    void patchNonExistingUgovor() throws Exception {
        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();
        ugovor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUgovorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ugovor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ugovor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUgovor() throws Exception {
        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();
        ugovor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUgovorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ugovor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUgovor() throws Exception {
        int databaseSizeBeforeUpdate = ugovorRepository.findAll().size();
        ugovor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUgovorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ugovor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ugovor in the database
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUgovor() throws Exception {
        // Initialize the database
        ugovorRepository.saveAndFlush(ugovor);

        int databaseSizeBeforeDelete = ugovorRepository.findAll().size();

        // Delete the ugovor
        restUgovorMockMvc
            .perform(delete(ENTITY_API_URL_ID, ugovor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ugovor> ugovorList = ugovorRepository.findAll();
        assertThat(ugovorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
