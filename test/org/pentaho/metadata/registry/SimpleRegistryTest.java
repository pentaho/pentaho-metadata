package org.pentaho.metadata.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SimpleRegistryTest {

	@Test
	public void testFactory () throws Exception {
		
		RegistryFactory factory = RegistryFactory.getInstance();
		Assert.assertNotNull( "Factory is null", factory );
		
		SimpleFileRegistry metadataRegistry = new SimpleFileRegistry();
		metadataRegistry.setFilePath("bin/mdregtest.xml");
		Assert.assertEquals("File path is wrong", "bin/mdregtest.xml", metadataRegistry.getFilePath());

		metadataRegistry.init();
		
		Assert.assertTrue("Init failed", metadataRegistry.isInitialized());
		
		metadataRegistry.clear();
		
		factory.setMetadataRegistry(metadataRegistry);
		
		IMetadataRegistry metadataRegistry2 = factory.getMetadataRegistry();

		Assert.assertEquals("Registry is wrong", metadataRegistry, metadataRegistry2);
		
		Entity ktr1 = new Entity("ktr1", "My Trans", Type.TYPE_TRANSFORMATION.getId());
		String attrValue1 = "attribute value 1";
		byte bytes[] = new byte[] {97,05,97};
		String attrValue2 = "attribute\tvalue 2\n"+new String(bytes);
		ktr1.setAttribute("attr1", attrValue1);
		ktr1.setAttribute("attr2", attrValue2);
		Entity table1 = new Entity("table1", "", Type.TYPE_PHYSICAL_TABLE.getId());
		Entity model1 = new Entity("model1", "my model", Type.TYPE_OLAP_MODEL.getId());
		Entity view1 = new Entity("view1", "my view", Type.TYPE_ANALYZER_VIEW.getId());

		metadataRegistry.addEntity(ktr1);
		metadataRegistry.addEntity(table1);
		metadataRegistry.addEntity(model1);
		metadataRegistry.addEntity(view1);
		
		Entity tmp = metadataRegistry.getEntity(ktr1.getId(), Type.TYPE_TRANSFORMATION.getId());
		Assert.assertNotNull("Entity is null",tmp);
		Assert.assertEquals("Entity id is wrong", ktr1.getId(), tmp.getId());
		Assert.assertEquals("Entity attribute is wrong", attrValue1, tmp.getAttribute("attr1"));
		Assert.assertEquals("Entity attribute is wrong", attrValue2, tmp.getAttribute("attr2"));
		
		Link link1 = new Link(ktr1.getId(), ktr1.getTypeId(), Verb.VERB_POPULATES.getId(), table1.getId(), table1.getTypeId());
		Link link2 = new Link(model1.getId(), model1.getTypeId(), Verb.VERB_USES.getId(), table1.getId(), table1.getTypeId());
		Link link3 = new Link(view1.getId(), view1.getTypeId(), Verb.VERB_USES.getId(), model1.getId(), model1.getTypeId() );
		
		metadataRegistry.addLink(link1);
		metadataRegistry.addLink(link2);
		metadataRegistry.addLink(link3);
		
		List<Link> links = metadataRegistry.findObjectLinks(ktr1.getId(), Verb.VERB_POPULATES.getId(), null);
		Assert.assertEquals("id list is wrong size", 1, links.size());
		Assert.assertEquals("id is wrong size", table1.getId(), links.get(0).getObjectId());

		links = metadataRegistry.findObjectLinks(ktr1.getId(), Verb.VERB_POPULATES.getId(), Type.TYPE_PHYSICAL_TABLE.getId());
		Assert.assertEquals("id list is wrong size", 1, links.size());
		Assert.assertEquals("id is wrong size", table1.getId(), links.get(0).getObjectId());

		links = metadataRegistry.findObjectLinks(ktr1.getId(), Verb.VERB_POPULATES.getId(), "bogus");
		Assert.assertEquals("id list is wrong size", 0, links.size());
		
		links = metadataRegistry.findSubjectLinks(table1.getId(), Verb.VERB_USES.getId(), null);
		Assert.assertEquals("id list is wrong size", 1, links.size());
		Assert.assertEquals("id is wrong size", model1.getId(), links.get(0).getSubjectId());

		links = metadataRegistry.findSubjectLinks(table1.getId(), Verb.VERB_USES.getId(), Type.TYPE_OLAP_MODEL.getId());
		Assert.assertEquals("id list is wrong size", 1, links.size());
		Assert.assertEquals("id is wrong size", model1.getId(), links.get(0).getSubjectId());

		links = metadataRegistry.findSubjectLinks(table1.getId(), Verb.VERB_USES.getId(), "bogus");
		Assert.assertEquals("id list is wrong size", 0, links.size());

		links = metadataRegistry.findSubjectLinks(view1.getId(), Verb.VERB_POPULATES.getId(), null);
		Assert.assertEquals("id list is wrong size", 0, links.size());
		
		links = metadataRegistry.findDirectLinks(table1.getId(), (String) null);
		Assert.assertEquals("id list is wrong size", 2, links.size());
		Assert.assertEquals("id is wrong size", ktr1.getId(), links.get(0).getSubjectId());
		Assert.assertEquals("id is wrong size", model1.getId(), links.get(1).getSubjectId());

		links = metadataRegistry.findDirectLinks(table1.getId(), Type.TYPE_OLAP_MODEL.getId());
		Assert.assertEquals("id list is wrong size", 1, links.size());
		Assert.assertEquals("id is wrong size", model1.getId(), links.get(0).getSubjectId());
		
		// find all links to the table

		List<Entity> entities = metadataRegistry.findAllLinkedEntities(table1.getId(), (String) null);
		Assert.assertEquals("Entity list is wrong size", 3, entities.size());
		Assert.assertTrue(ktr1.getId().equals(entities.get(0).getId()) || ktr1.getId().equals(entities.get(1).getId()) || ktr1.getId().equals(entities.get(2).getId()));
		Assert.assertTrue(model1.getId().equals(entities.get(0).getId()) || model1.getId().equals(entities.get(1).getId()) || model1.getId().equals(entities.get(2).getId()));
		Assert.assertTrue(view1.getId().equals(entities.get(0).getId()) || view1.getId().equals(entities.get(1).getId()) || view1.getId().equals(entities.get(2).getId()));
		
		entities = metadataRegistry.findAllLinkedEntities(table1.getId(), Type.TYPE_ANALYZER_VIEW.getId());
		Assert.assertEquals("id list is wrong size", 1, entities.size());
		Assert.assertTrue(view1.getId().equals(entities.get(0).getId()) );
		
		entities = metadataRegistry.getEntities("bogus");
		Assert.assertNotNull("Entites is null", entities);
		Assert.assertEquals("Entity list is wrong size", 0, entities.size());
		
		entities = metadataRegistry.getEntities(ktr1.getId());
		Assert.assertNotNull("Entites is null", entities);
		Assert.assertEquals("Entity list is wrong size", 1, entities.size());
		Assert.assertEquals("Entity list is wrong size", ktr1.getId(), entities.get(0).getId());
		
		List<Namespace> namespaces = metadataRegistry.getNamespaces();
		List<Namespace> namespaces2 = new ArrayList<Namespace>();
		metadataRegistry.setNamespaces(namespaces2);
		Assert.assertEquals("Namespaces is wrong", namespaces2, metadataRegistry.getNamespaces());
		metadataRegistry.setNamespaces(namespaces);
		Assert.assertEquals("Namespaces is wrong", namespaces, metadataRegistry.getNamespaces());
		
		List<Type> types = metadataRegistry.getTypes();
		List<Type> types2 = new ArrayList<Type>();
		metadataRegistry.setTypes(types2);
		Assert.assertEquals("Namespaces is wrong", types2, metadataRegistry.getTypes());
		metadataRegistry.setTypes(types);
		Assert.assertEquals("Namespaces is wrong", types, metadataRegistry.getTypes());
		
		List<Verb> verbs = metadataRegistry.getVerbs();
		List<Verb> verbs2 = new ArrayList<Verb>();
		metadataRegistry.setVerbs(verbs2);
		Assert.assertEquals("Namespaces is wrong", verbs2, metadataRegistry.getVerbs());
		metadataRegistry.setVerbs(verbs);
		Assert.assertEquals("Namespaces is wrong", verbs, metadataRegistry.getVerbs());
		
		Map<String, Map<String,Entity>> entities1 = metadataRegistry.getEntities();
		Map<String, Map<String,Entity>> entities2 = new HashMap<String, Map<String,Entity>>();
		metadataRegistry.setEntities(entities2);
		Assert.assertEquals("Namespaces is wrong", entities2, metadataRegistry.getEntities());
		metadataRegistry.setEntities(entities1);
		Assert.assertEquals("Namespaces is wrong", entities1, metadataRegistry.getEntities());
		
	}
	
	@Test
	public void testEntity() {

		Entity entity = new Entity();
		Assert.assertNull(entity.getTitle());
		Assert.assertNull(entity.getId());
		Assert.assertNull(entity.getTypeId());
		entity.setTitle("title1");
		entity.setId("id1");
		entity.setTypeId("type1");
		Assert.assertEquals("Entity title is wrong", "title1", entity.getTitle());
		Assert.assertEquals("Entity id is wrong", "id1", entity.getId());
		Assert.assertEquals("Entity type is wrong", "type1", entity.getTypeId());
		
		entity = new Entity("entity1", "a view", Type.TYPE_ANALYZER_VIEW.getId());
		Assert.assertEquals("Entity id is wrong", "entity1", entity.getId());
		Assert.assertEquals("Entity type is wrong", Type.TYPE_ANALYZER_VIEW.getId(), entity.getTypeId());

		entity.setId("entity2");
		entity.setTypeId(Type.TYPE_PHYSICAL_TABLE.getId());
		Assert.assertEquals("Entity id is wrong", "entity2", entity.getId());
		Assert.assertEquals("Entity type is wrong", Type.TYPE_PHYSICAL_TABLE.getId(), entity.getTypeId());
		
		entity.setAttribute("attr1", "value1");

		Assert.assertEquals("Entity attribute is wrong", "value1", entity.getAttribute("attr1"));
		
		Map<String,String> attributes = new HashMap<String,String>();
		attributes.put("attr2", "value2");
		attributes.put("attr3", "value3");
		entity.setAttributes(attributes);
		Assert.assertNull("Entity attribute1 is wrong", entity.getAttribute("attr1"));
		Assert.assertEquals("Entity attribute2 is wrong", "value2", entity.getAttribute("attr2"));	
		Assert.assertEquals("Entity attribute3 is wrong", "value3", entity.getAttribute("attr3"));
		
		Map<String,? extends Serializable> attributes2 = entity.getAttributes();
		Assert.assertEquals("Entity attribute2 is wrong", "value2", attributes2.get("attr2"));	
		Assert.assertEquals("Entity attribute3 is wrong", "value3", attributes2.get("attr3"));
		
	}
	
	@Test
	public void testLink() {

		Link link = new Link();
		Assert.assertNull(link.getObjectId());
		Assert.assertNull(link.getObjectTypeId());
		Assert.assertNull(link.getSubjectId());
		Assert.assertNull(link.getSubjectTypeId());
		Assert.assertNull(link.getVerbId());

		link.setObjectId("object1");
		link.setObjectTypeId("type1");
		link.setSubjectId("subject1");
		link.setSubjectTypeId("type2");
		link.setVerbId("verb1");
		Assert.assertEquals("Link object id is wrong", "object1", link.getObjectId());
		Assert.assertEquals("Link object type is wrong", "type1", link.getObjectTypeId());
		Assert.assertEquals("Link subject id is wrong", "subject1", link.getSubjectId());
		Assert.assertEquals("Link subject type is wrong", "type2", link.getSubjectTypeId());
		Assert.assertEquals("Link verb is wrong", "verb1", link.getVerbId());

		Link link2 = new Link( "subject2", "type3", "verb2", "object2", "type4");
		Assert.assertEquals("Link object id is wrong", "object2", link2.getObjectId());
		Assert.assertEquals("Link object type is wrong", "type4", link2.getObjectTypeId());
		Assert.assertEquals("Link subject id is wrong", "subject2", link2.getSubjectId());
		Assert.assertEquals("Link subject type is wrong", "type3", link2.getSubjectTypeId());
		Assert.assertEquals("Link verb is wrong", "verb2", link2.getVerbId());
	
		Entity subject = new Entity("trans1", "name3", Type.TYPE_TRANSFORMATION.getId());
		Entity object = new Entity("table1", "name4", Type.TYPE_PHYSICAL_TABLE.getId());
		Link link3 = new Link( subject, Verb.VERB_POPULATES, object );
		Assert.assertEquals("Link object id is wrong", "table1", link3.getObjectId());
		Assert.assertEquals("Link object type is wrong", Type.TYPE_PHYSICAL_TABLE.getId(), link3.getObjectTypeId());
		Assert.assertEquals("Link subject id is wrong", "trans1", link3.getSubjectId());
		Assert.assertEquals("Link subject type is wrong", Type.TYPE_TRANSFORMATION.getId(), link3.getSubjectTypeId());
		Assert.assertEquals("Link verb is wrong", Verb.VERB_POPULATES.getId(), link3.getVerbId());
		
	}

	@Test
	public void testType() {

		Type type = new Type();
		Assert.assertNull(type.getId());
		Assert.assertNull(type.getNamespaceId());
		Assert.assertNull(type.getType());

		type.setId("id1");
		type.setNamespaceId("namespace1");
		type.setType("type1");
		Assert.assertEquals("Link id is wrong", "id1", type.getId());
		Assert.assertEquals("Link namespace is wrong", "namespace1", type.getNamespaceId());
		Assert.assertEquals("Link type is wrong", "type1", type.getType());

		Type type2 = new Type( "id2", "namespace2", "type2");
		Assert.assertEquals("Link id is wrong", "id2", type2.getId());
		Assert.assertEquals("Link namespace is wrong", "namespace2", type2.getNamespaceId());
		Assert.assertEquals("Link type is wrong", "type2", type2.getType());
		
	}

	@Test
	public void testVerb() {

		Verb verb = new Verb();
		Assert.assertNull(verb.getId());
		Assert.assertNull(verb.getNamespaceId());
		Assert.assertNull(verb.getVerbId());

		verb.setId("id1");
		verb.setNamespaceId("namespace1");
		verb.setVerbId("verb1");
		Assert.assertEquals("Link id is wrong", "id1", verb.getId());
		Assert.assertEquals("Link namespace is wrong", "namespace1", verb.getNamespaceId());
		Assert.assertEquals("Link type is wrong", "verb1", verb.getVerbId());

		Verb verb2 = new Verb( "id2", "namespace2", "verb2");
		Assert.assertEquals("Link id is wrong", "id2", verb2.getId());
		Assert.assertEquals("Link namespace is wrong", "namespace2", verb2.getNamespaceId());
		Assert.assertEquals("Link type is wrong", "verb2", verb2.getVerbId());
		
	}
		
	@Test
	public void testTypeLink() {

		TypeLink typeLink = new TypeLink();
		Assert.assertNull(typeLink.getObjectTypeId());
		Assert.assertNull(typeLink.getSubjectTypeId());
		Assert.assertNull(typeLink.getVerbId());

		typeLink.setObjectTypeId("type1");
		typeLink.setSubjectTypeId("type2");
		typeLink.setVerbId("verb1");
		Assert.assertEquals("Link object type is wrong", "type1", typeLink.getObjectTypeId());
		Assert.assertEquals("Link subject type is wrong", "type2", typeLink.getSubjectTypeId());
		Assert.assertEquals("Link verb is wrong", "verb1", typeLink.getVerbId());

		TypeLink typeLink2 = new TypeLink( "type3", "verb2", "type4");
		Assert.assertEquals("Link object type is wrong", "type4", typeLink2.getObjectTypeId());
		Assert.assertEquals("Link subject type is wrong", "type3", typeLink2.getSubjectTypeId());
		Assert.assertEquals("Link verb is wrong", "verb2", typeLink2.getVerbId());
	}	
	@Test
	public void testNamespace() {

		Namespace namespace = new Namespace();
		Assert.assertNull(namespace.getId());
		Assert.assertNull(namespace.getTool());

		namespace.setId("id1");
		namespace.setTool("tool1");
		Assert.assertEquals("Namespace id is wrong", "id1", namespace.getId());
		Assert.assertEquals("Namespace tool is wrong", "tool1", namespace.getTool());

		Namespace namespace2 = new Namespace( "id2");
		Assert.assertEquals("Namespace id is wrong", "id2", namespace2.getId());
		
	}	
	
	@Test
	public void testSaveLoad() throws Exception {

		RegistryFactory factory = RegistryFactory.getInstance();

		SimpleFileRegistry metadataRegistry = (SimpleFileRegistry) factory.getMetadataRegistry();
		
		metadataRegistry.clear();
		
		Entity ktr1 = new Entity("ktr1", "My Trans", Type.TYPE_TRANSFORMATION.getId());
		String attrValue1 = "attribute value 1";
		byte bytes[] = new byte[] {97,05,97};
		String attrValue2 = "attribute\tvalue 2\n"+new String(bytes);
		ktr1.setAttribute("attr1", attrValue1);
		ktr1.setAttribute("attr2", attrValue2);
		Entity table1 = new Entity("table1", null, Type.TYPE_PHYSICAL_TABLE.getId());
		Entity model1 = new Entity("model1", "my model", Type.TYPE_OLAP_MODEL.getId());
		Entity view1 = new Entity("view1", "my view", Type.TYPE_ANALYZER_VIEW.getId());

		metadataRegistry.addEntity(ktr1);
		metadataRegistry.addEntity(table1);
		metadataRegistry.addEntity(model1);
		metadataRegistry.addEntity(view1);
	
		Link link1 = new Link(ktr1.getId(), ktr1.getTypeId(), Verb.VERB_POPULATES.getId(), table1.getId(), table1.getTypeId());
		Link link2 = new Link(model1.getId(), model1.getTypeId(), Verb.VERB_USES.getId(), table1.getId(), table1.getTypeId());
		Link link3 = new Link(view1.getId(), view1.getTypeId(), Verb.VERB_USES.getId(), model1.getId(), model1.getTypeId() );
		
		metadataRegistry.addLink(link1);
		metadataRegistry.addLink(link2);
		metadataRegistry.addLink(link3);
		
		Entity tmp = metadataRegistry.getEntity(ktr1.getId(), Type.TYPE_TRANSFORMATION.getId());
		Assert.assertEquals("Entity id is wrong", ktr1.getId(), tmp.getId());
		Assert.assertEquals("Entity attribute is wrong", attrValue1, tmp.getAttribute("attr1"));
		Assert.assertEquals("Entity attribute is wrong", attrValue2, tmp.getAttribute("attr2"));
		Assert.assertEquals("links list is wrong size", 3, metadataRegistry.getLinks().size());
		
		Assert.assertEquals("Wrong number of verbs", 5, metadataRegistry.getVerbs().size());
		Assert.assertEquals("Wrong verb", Verb.VERB_POPULATES.getId(), metadataRegistry.getVerbs().get(0).getId());
		
		Assert.assertEquals("Wrong number of namespaces", 5, metadataRegistry.getNamespaces().size());
		Assert.assertEquals("Wrong namespace", Namespace.NAMESPACE_GLOBAL.getId(), metadataRegistry.getNamespaces().get(0).getId());
		
		Assert.assertEquals("Wrong number of types", 13, metadataRegistry.getTypes().size());
		Assert.assertEquals("Wrong tpe", Type.TYPE_TRANSFORMATION.getId(), metadataRegistry.getTypes().get(0).getId());
		
		Assert.assertEquals("Wrong number of type links", 18, metadataRegistry.getTypeLinks().size());
		Assert.assertEquals("Wrong tpe", Verb.VERB_USES.getId(), metadataRegistry.getTypeLinks().get(0).getVerbId());
		
		metadataRegistry.commit();
		
		metadataRegistry.clear();

		tmp = metadataRegistry.getEntity(ktr1.getId(), Type.TYPE_TRANSFORMATION.getId());
		Assert.assertNull("Entity id is wrong", tmp);
		Assert.assertEquals("links list is wrong size", 0, metadataRegistry.getLinks().size());

		metadataRegistry.load();
		tmp = metadataRegistry.getEntity(ktr1.getId(), Type.TYPE_TRANSFORMATION.getId());
		Assert.assertNotNull("Entity is null", tmp);
		Assert.assertEquals("Entity id is wrong", ktr1.getId(), tmp.getId());
		Assert.assertEquals("links list is wrong size", 3, metadataRegistry.getLinks().size());
		Assert.assertEquals("Entity attribute is wrong", attrValue1, tmp.getAttribute("attr1"));
		Assert.assertEquals("Entity attribute is wrong", attrValue2, tmp.getAttribute("attr2"));
		
	}
	
	@Test
	public void testList() {
		
		RegistryFactory factory = RegistryFactory.getInstance();
		SimpleFileRegistry metadataRegistry = (SimpleFileRegistry) factory.getMetadataRegistry();

		List<Entity> list = metadataRegistry.getEntitiesOfType(Type.TYPE_PHYSICAL_TABLE.getId(), null, false);
		Assert.assertEquals( "list is wrong size", 1, list.size());
		Assert.assertEquals("id is wrong", "table1", list.get(0).getId());
		Assert.assertEquals("type is wrong", Type.TYPE_PHYSICAL_TABLE.getId(), list.get(0).getTypeId());

		list = metadataRegistry.getEntitiesOfType(Type.TYPE_PHYSICAL_TABLE.getId(), "bogus", true);
		Assert.assertEquals( "list is wrong size", 0, list.size());

		list = metadataRegistry.getEntitiesOfType(Type.TYPE_ANALYZER_VIEW.getId(), "view1", true);
		Assert.assertEquals( "list is wrong size", 1, list.size());
		Assert.assertEquals("id is wrong", "view1", list.get(0).getId());
		Assert.assertEquals("type is wrong", Type.TYPE_ANALYZER_VIEW.getId(), list.get(0).getTypeId());

		list = metadataRegistry.getEntitiesOfType(Type.TYPE_PHYSICAL_TABLE.getId(), "able", true);
		Assert.assertEquals( "list is wrong size", 1, list.size());
		Assert.assertEquals("id is wrong", "table1", list.get(0).getId());
		Assert.assertEquals("type is wrong", Type.TYPE_PHYSICAL_TABLE.getId(), list.get(0).getTypeId());

	}
	
	@Test
	public void testDelete() {

		RegistryFactory factory = RegistryFactory.getInstance();

		SimpleFileRegistry metadataRegistry = (SimpleFileRegistry) factory.getMetadataRegistry();
		
		metadataRegistry.clear();
		
		Entity ktr1 = new Entity("ktr1", "My Trans", Type.TYPE_TRANSFORMATION.getId());
		Entity table1 = new Entity("table1", null, Type.TYPE_PHYSICAL_TABLE.getId());
		Entity bogus = new Entity("bogus", "my view", Type.TYPE_ANALYZER_VIEW.getId());

		metadataRegistry.addEntity(ktr1);
		metadataRegistry.addEntity(table1);
	
		Link link1 = new Link(ktr1.getId(), ktr1.getTypeId(), Verb.VERB_POPULATES.getId(), table1.getId(), table1.getTypeId());

		Link bogusLink = new Link(ktr1.getId(), ktr1.getTypeId(), Verb.VERB_POPULATES.getId(), bogus.getId(), bogus.getTypeId());
		
		metadataRegistry.addLink(link1);
		
		Entity tmp = metadataRegistry.getEntity(ktr1.getId(), Type.TYPE_TRANSFORMATION.getId());
		Assert.assertNotNull("Entity is null", tmp);
		Assert.assertEquals("Entities list is wrong size", 2, metadataRegistry.getEntities().size());
		Assert.assertEquals("Links list is wrong size", 1, metadataRegistry.getLinks().size());
		
		boolean result = metadataRegistry.removeEntity(bogus);
		Assert.assertFalse("Wrong result", result);
		Assert.assertEquals("Entities list is wrong size", 2, metadataRegistry.getEntities().size());
		Assert.assertEquals("Links list is wrong size", 1, metadataRegistry.getLinks().size());

		result = metadataRegistry.removeEntity(ktr1);
		Assert.assertTrue("Wrong result", result);
		tmp = metadataRegistry.getEntity(ktr1.getId(), Type.TYPE_TRANSFORMATION.getId());
		Assert.assertNull("Entity is not null", tmp);
		Assert.assertEquals("Entities list is wrong size", 1, metadataRegistry.getEntities().size());
		Assert.assertEquals("Links list is wrong size", 1, metadataRegistry.getLinks().size());

		result = metadataRegistry.removeEntity(table1);
		Assert.assertTrue("Wrong result", result);
		tmp = metadataRegistry.getEntity(table1.getId(), Type.TYPE_PHYSICAL_TABLE.getId());
		Assert.assertNull("Entity is not null", tmp);
		Assert.assertEquals("Entities list is wrong size", 0, metadataRegistry.getEntities().size());
		Assert.assertEquals("Links list is wrong size", 1, metadataRegistry.getLinks().size());
		
		result = metadataRegistry.removeLink(bogusLink);
		Assert.assertFalse("Wrong result", result);
		Assert.assertEquals("Links list is wrong size", 1, metadataRegistry.getLinks().size());
		
		result = metadataRegistry.removeLink(link1);
		Assert.assertTrue("Wrong result", result);
		Assert.assertEquals("Links list is wrong size", 0, metadataRegistry.getLinks().size());
		
	}
	
}
