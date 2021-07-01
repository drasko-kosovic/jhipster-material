package material.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import material.IntegrationTest;
import material.domain.Ponudjaci;
import material.repository.PonudjaciRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PonudjaciResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PonudjaciResourceIT {

    private static final String DEFAULT_IME = "AAAAAAAAAA";
    private static final String UPDATED_IME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ponudjacis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PonudjaciRepository ponudjaciRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPonudjaciMockMvc;

    private Ponudjaci ponudjaci;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ponudjaci createEntity(EntityManager em) {
        Ponudjaci ponudjaci = new Ponudjaci().ime(DEFAULT_IME);
        return ponudjaci;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ponudjaci createUpdatedEntity(EntityManager em) {
        Ponudjaci ponudjaci = new Ponudjaci().ime(UPDATED_IME);
        return ponudjaci;
    }

    @BeforeEach
    public void initTest() {
        ponudjaci = createEntity(em);
    }

    @Test
    @Transactional
    void createPonudjaci() throws Exception {
        int databaseSizeBeforeCreate = ponudjaciRepository.findAll().size();
        // Create the Ponudjaci
        restPonudjaciMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponudjaci)))
            .andExpect(status().isCreated());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeCreate + 1);
        Ponudjaci testPonudjaci = ponudjaciList.get(ponudjaciList.size() - 1);
        assertThat(testPonudjaci.getIme()).isEqualTo(DEFAULT_IME);
    }

    @Test
    @Transactional
    void createPonudjaciWithExistingId() throws Exception {
        // Create the Ponudjaci with an existing ID
        ponudjaci.setId(1L);

        int databaseSizeBeforeCreate = ponudjaciRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPonudjaciMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponudjaci)))
            .andExpect(status().isBadRequest());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkImeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ponudjaciRepository.findAll().size();
        // set the field null
        ponudjaci.setIme(null);

        // Create the Ponudjaci, which fails.

        restPonudjaciMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponudjaci)))
            .andExpect(status().isBadRequest());

        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPonudjacis() throws Exception {
        // Initialize the database
        ponudjaciRepository.saveAndFlush(ponudjaci);

        // Get all the ponudjaciList
        restPonudjaciMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ponudjaci.getId().intValue())))
            .andExpect(jsonPath("$.[*].ime").value(hasItem(DEFAULT_IME)));
    }

    @Test
    @Transactional
    void getPonudjaci() throws Exception {
        // Initialize the database
        ponudjaciRepository.saveAndFlush(ponudjaci);

        // Get the ponudjaci
        restPonudjaciMockMvc
            .perform(get(ENTITY_API_URL_ID, ponudjaci.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ponudjaci.getId().intValue()))
            .andExpect(jsonPath("$.ime").value(DEFAULT_IME));
    }

    @Test
    @Transactional
    void getNonExistingPonudjaci() throws Exception {
        // Get the ponudjaci
        restPonudjaciMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPonudjaci() throws Exception {
        // Initialize the database
        ponudjaciRepository.saveAndFlush(ponudjaci);

        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();

        // Update the ponudjaci
        Ponudjaci updatedPonudjaci = ponudjaciRepository.findById(ponudjaci.getId()).get();
        // Disconnect from session so that the updates on updatedPonudjaci are not directly saved in db
        em.detach(updatedPonudjaci);
        updatedPonudjaci.ime(UPDATED_IME);

        restPonudjaciMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPonudjaci.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPonudjaci))
            )
            .andExpect(status().isOk());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
        Ponudjaci testPonudjaci = ponudjaciList.get(ponudjaciList.size() - 1);
        assertThat(testPonudjaci.getIme()).isEqualTo(UPDATED_IME);
    }

    @Test
    @Transactional
    void putNonExistingPonudjaci() throws Exception {
        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();
        ponudjaci.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPonudjaciMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ponudjaci.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ponudjaci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPonudjaci() throws Exception {
        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();
        ponudjaci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudjaciMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ponudjaci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPonudjaci() throws Exception {
        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();
        ponudjaci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudjaciMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponudjaci)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePonudjaciWithPatch() throws Exception {
        // Initialize the database
        ponudjaciRepository.saveAndFlush(ponudjaci);

        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();

        // Update the ponudjaci using partial update
        Ponudjaci partialUpdatedPonudjaci = new Ponudjaci();
        partialUpdatedPonudjaci.setId(ponudjaci.getId());

        restPonudjaciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPonudjaci.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPonudjaci))
            )
            .andExpect(status().isOk());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
        Ponudjaci testPonudjaci = ponudjaciList.get(ponudjaciList.size() - 1);
        assertThat(testPonudjaci.getIme()).isEqualTo(DEFAULT_IME);
    }

    @Test
    @Transactional
    void fullUpdatePonudjaciWithPatch() throws Exception {
        // Initialize the database
        ponudjaciRepository.saveAndFlush(ponudjaci);

        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();

        // Update the ponudjaci using partial update
        Ponudjaci partialUpdatedPonudjaci = new Ponudjaci();
        partialUpdatedPonudjaci.setId(ponudjaci.getId());

        partialUpdatedPonudjaci.ime(UPDATED_IME);

        restPonudjaciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPonudjaci.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPonudjaci))
            )
            .andExpect(status().isOk());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
        Ponudjaci testPonudjaci = ponudjaciList.get(ponudjaciList.size() - 1);
        assertThat(testPonudjaci.getIme()).isEqualTo(UPDATED_IME);
    }

    @Test
    @Transactional
    void patchNonExistingPonudjaci() throws Exception {
        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();
        ponudjaci.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPonudjaciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ponudjaci.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ponudjaci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPonudjaci() throws Exception {
        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();
        ponudjaci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudjaciMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ponudjaci))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPonudjaci() throws Exception {
        int databaseSizeBeforeUpdate = ponudjaciRepository.findAll().size();
        ponudjaci.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudjaciMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ponudjaci))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ponudjaci in the database
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePonudjaci() throws Exception {
        // Initialize the database
        ponudjaciRepository.saveAndFlush(ponudjaci);

        int databaseSizeBeforeDelete = ponudjaciRepository.findAll().size();

        // Delete the ponudjaci
        restPonudjaciMockMvc
            .perform(delete(ENTITY_API_URL_ID, ponudjaci.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ponudjaci> ponudjaciList = ponudjaciRepository.findAll();
        assertThat(ponudjaciList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
