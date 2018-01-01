package com.buschmais.jqassistant.plugin.ejb3.test;

import java.io.IOException;

import com.buschmais.jqassistant.core.analysis.api.Result;
import com.buschmais.jqassistant.core.analysis.api.rule.RuleException;
import com.buschmais.jqassistant.plugin.ejb3.test.set.beans.MessageDrivenBean;
import com.buschmais.jqassistant.plugin.ejb3.test.set.beans.SingletonBean;
import com.buschmais.jqassistant.plugin.ejb3.test.set.beans.StatefulBean;
import com.buschmais.jqassistant.plugin.ejb3.test.set.beans.StatelessLocalBean;
import com.buschmais.jqassistant.plugin.ejb3.test.set.beans.StatelessRemoteBean;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.junit.Test;

import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Tests for the EJB3 concepts.
 */
public class Ejb3IT extends AbstractJavaPluginIT {

    /**
     * Verifies the concept "ejb3:StatelessSessionBean".
     *
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void statelessSessionBean() throws Exception {
        scanClasses(StatelessLocalBean.class);
        assertThat(applyConcept("ejb3:StatelessSessionBean").getStatus(), equalTo(Result.Status.SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (ejb:Type:Stateless:Ejb) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(StatelessLocalBean.class)));
        store.commitTransaction();
    }

    /**
     * Verifies the concept "ejb3:StatefulSessionBean".
     *
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void statefulSessionBean() throws Exception {
        scanClasses(StatefulBean.class);
        assertThat(applyConcept("ejb3:StatefulSessionBean").getStatus(), equalTo(Result.Status.SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (ejb:Type:Stateful:Ejb) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(StatefulBean.class)));
        store.commitTransaction();
    }

    /**
     * Verifies the concept "ejb3:SingletonBean".
     *
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void singletonBean() throws Exception {
        scanClasses(SingletonBean.class);
        assertThat(applyConcept("ejb3:SingletonBean").getStatus(), equalTo(Result.Status.SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (ejb:Type:Singleton:Ejb) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(SingletonBean.class)));
        store.commitTransaction();
    }

    /**
     * Verifies the concept "ejb3:MessageDrivenBean".
     *
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void messageDrivenBean() throws Exception {
        scanClasses(MessageDrivenBean.class);
        assertThat(applyConcept("ejb3:MessageDrivenBean").getStatus(), equalTo(Result.Status.SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (ejb:Type:MessageDriven:Ejb) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(MessageDrivenBean.class)));
        store.commitTransaction();
    }

    /**
     * Verifies the concept "ejb3:Local".
     *
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void localSessionBean() throws Exception {
        scanClasses(StatelessLocalBean.class);
        assertThat(applyConcept("ejb3:Local").getStatus(), equalTo(Result.Status.SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (ejb:Type:Local:Ejb) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(StatelessLocalBean.class)));
        store.commitTransaction();
    }

    /**
     * Verifies the concept "ejb3:Remote".
     *
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void remoteSessionBean() throws Exception {
        scanClasses(StatelessRemoteBean.class);
        assertThat(applyConcept("ejb3:Remote").getStatus(), equalTo(Result.Status.SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (ejb:Type:Remote:Ejb) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(StatelessRemoteBean.class)));
        store.commitTransaction();
    }

    /**
     * Verifies the analysis group "ejb3:EnterpriseJavaBean".
     *
     * @throws java.io.IOException
     *             If the test fails.
     */
    @Test
    public void enterpriseJavaBean() throws IOException, RuleException {
        scanClasses(StatelessLocalBean.class, StatelessRemoteBean.class, StatefulBean.class, MessageDrivenBean.class);
        executeGroup("ejb3:EJB");
        store.beginTransaction();
        assertThat(
                query("MATCH (ejb:Type:Ejb) RETURN ejb").getColumn("ejb"),
                allOf(hasItem(typeDescriptor(StatelessLocalBean.class)), hasItem(typeDescriptor(StatelessRemoteBean.class)),
                        hasItem(typeDescriptor(StatefulBean.class)), hasItem(typeDescriptor(MessageDrivenBean.class))));
        assertThat(query("MATCH (ejb:Type:Ejb:Local) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(StatelessLocalBean.class)));
        assertThat(query("MATCH (ejb:Type:Ejb:Remote) RETURN ejb").getColumn("ejb"), hasItem(typeDescriptor(StatelessRemoteBean.class)));
        store.commitTransaction();
    }
}
