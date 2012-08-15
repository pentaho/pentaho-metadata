package org.pentaho.metadata;

import junit.framework.Assert;
import org.junit.Test;
import org.pentaho.metadata.datatable.DataTable;
import org.pentaho.metadata.datatable.Types;
import org.pentaho.metadata.model.concept.types.Alignment;
import org.pentaho.metadata.model.thin.Element;
import org.pentaho.metadata.model.thin.MetadataModelsService;
import org.pentaho.metadata.model.thin.Model;
import org.pentaho.metadata.model.thin.ModelInfo;
import org.pentaho.metadata.model.thin.Provider;
import org.pentaho.metadata.model.thin.Query;

@SuppressWarnings({"all"})
public class MetadataModelsServiceTest {

	static {
		TestModelProvider.getInstance();
	}

	@Test
	public void testProvider() {
		Provider provider = new Provider("id1", "name1");
		
		Assert.assertEquals("Wrong id", "id1", provider.getId());
		Assert.assertEquals("Wrong name", "name1", provider.getName());

		provider.setId("id2");
		provider.setName("name2");
		Assert.assertEquals("Wrong id", "id2", provider.getId());
		Assert.assertEquals("Wrong name", "name2", provider.getName());
		
	}
	
	@Test
	public void testGetModelList() {
		MetadataModelsService stats = new MetadataModelsService();
		
		ModelInfo infos[] = stats.getModelList(null,null,null);
		Assert.assertNotNull("Model infos is null",infos);
		Assert.assertEquals("Wrong number of model infos", 1, infos.length);
		Assert.assertEquals("Domain id is wrong", TestModelProvider.GROUP_ID, infos[0].getGroupId());
		String id = TestModelProvider.getInstance().getModelList(null,null,null)[0].getId();
		Assert.assertEquals("Id is wrong", TestModelProvider.MODEL_ID, infos[0].getModelId());
		Assert.assertEquals("Id is wrong", id, infos[0].getId());
		Assert.assertNotNull("Name is null", infos[0].getName());
		Assert.assertNotNull("Description is null", infos[0].getDescription());
		
		infos = stats.getModelList("bogus",null,null);
		Assert.assertNotNull("Models is not null",infos);
		Assert.assertEquals("Wrong number of models", 0, infos.length);
		
		infos = stats.getModelList(TestModelProvider.PROVIDER_ID,"bogus",null);
		Assert.assertNotNull("Models is not null",infos);
		Assert.assertEquals("Wrong number of models", 0, infos.length);
		
		infos = stats.getModelList(TestModelProvider.PROVIDER_ID,TestModelProvider.GROUP_ID,"bogus");
		Assert.assertNotNull("Models is not null",infos);
		Assert.assertEquals("Wrong number of models", 0, infos.length);
		
		infos = stats.getModelList(TestModelProvider.PROVIDER_ID,TestModelProvider.GROUP_ID,TestModelProvider.MODEL_ID);
		Assert.assertEquals("Wrong number of models", 1, infos.length);
		
		Assert.assertEquals("Domain id is wrong", TestModelProvider.GROUP_ID, infos[0].getGroupId());
		Assert.assertEquals("Id is wrong", TestModelProvider.MODEL_ID, infos[0].getModelId());
		Assert.assertNotNull("Name is null", infos[0].getName());
		Assert.assertNotNull("Description is null", infos[0].getDescription());
		
	}
	
	@Test
	public void testGetModel() {
		MetadataModelsService svc = new MetadataModelsService();
		
		Model model = svc.getModel("");
		Assert.assertNull("Models is not null",model);
		
		model = svc.getModel("bogus");
		Assert.assertNull("Models is not null",model);
		
		model = svc.getModel(TestModelProvider.GROUP_ID);
		Assert.assertNull("Models is not null",model);

		String id = TestModelProvider.getInstance().getModelList(null,null,null)[0].getId();
		model = svc.getModel(id);
		Assert.assertNotNull("Model is null",model);
		Assert.assertEquals("Group id is wrong", TestModelProvider.GROUP_ID, model.getGroupId());
		Assert.assertEquals("Id is wrong", TestModelProvider.MODEL_ID, model.getModelId());
		Assert.assertNotNull("Name is null", model.getName());
		Assert.assertNotNull("Description is null", model.getDescription());
		
		Element elements[] = model.getElements();
		Assert.assertNotNull("Elements is null",elements);
		Assert.assertEquals("Wrong number of elements", 3, elements.length);
		
		Element e = elements[0];
		Assert.assertNull("Wrong element property", e.getAvailableAggregations());
		Assert.assertNotNull("Wrong element property", e.getCapabilities());
		Assert.assertEquals("Wrong element property", Types.TYPE_STRING.toString(), e.getDataType());
		Assert.assertEquals("Wrong element property", "NONE", e.getDefaultAggregation());
		Assert.assertEquals("Wrong element property", "Description 1", e.getDescription());
		Assert.assertEquals("Wrong element property", "DIMENSION", e.getElementType());
		Assert.assertEquals("Wrong element property", "element1", e.getId());
		Assert.assertEquals("Wrong element property", "Element 1", e.getName());
		Assert.assertEquals("Wrong element property", null, e.getParentId());
		Assert.assertEquals("Wrong element property", "NONE", e.getSelectedAggregation());
		Assert.assertEquals("Wrong element property", false, e.getIsQueryElement());
		
		e = elements[2];
		Assert.assertEquals("Wrong element property", "#,###.00", e.getFormatMask());
		Assert.assertEquals("Wrong element property", Alignment.RIGHT.name(), e.getHorizontalAlignment());
		Assert.assertEquals("Wrong element property", true, e.getIsQueryElement());
		Assert.assertEquals("Wrong element property", false, e.isHiddenForUser());
		
		Model model2 = new TestModelProvider().getModel(model.getId());
		Assert.assertTrue("Wrong hash code", model.hashCode() != 0);
		Assert.assertTrue("Wrong equals result", model.equals(model));
		Assert.assertFalse("Wrong equals result", model.equals(null));
		Assert.assertFalse("Wrong equals result", model.equals(this));
		Assert.assertTrue("Wrong equals result", model.equals(model2));
		
		String modelId = model.getModelId();
		model2.setModelId("bogus");
		Assert.assertFalse("Wrong equals result", model.equals(model2));
		model2.setModelId(model.getModelId());
		model2.setName(null);
		Assert.assertFalse("Wrong equals result", model.equals(model2));
		Assert.assertFalse("Wrong equals result", model2.equals(model));
		model2.setName("bogus");
		Assert.assertFalse("Wrong equals result", model2.equals(model));
	}
	
	
	@Test
	public void testBadQuery() throws InterruptedException {
		
		MetadataModelsService stats = new MetadataModelsService();
		
		// A query with no condition
		Query query = new Query();

		Element element1 = new Element();
		element1.setId("element1");
		Element element2 = new Element();
		element2.setId("element2");
		
		Element elements[] = new Element[] {element1, element2};
		query.setElements(elements);

		query.setSourceId("bogus");
		DataTable table = stats.executeQuery(query,-1);
		Assert.assertNull("Data table is not null", table);
		
		query.setSourceId(null);
		table = stats.executeQuery(query,-1);
		Assert.assertNull("Data table is not null", table);
		
	}
			
	@Test
	public void testQuery() throws InterruptedException {
		
		MetadataModelsService stats = new MetadataModelsService();
		
		// A query with no condition
		Query query = new Query();
		String id = TestModelProvider.getInstance().getModelList(null,null,null)[0].getId();
		query.setSourceId(id);
		Element element1 = new Element();
		element1.setId("element1");
		Element element2 = new Element();
		element2.setId("element2");
		element2.setSelectedAggregation("SUM");
		
		Element elements[] = new Element[] {element1, element2};
		query.setElements(elements);

		DataTable table = stats.executeQuery(query,-1);
		Assert.assertNotNull("Data table is null", table);
		
		Assert.assertEquals("Wrong number of columns", 2, table.getCols().length);
		Assert.assertEquals("Wrong column name", "element1", table.getCols()[0].getId());
		Assert.assertEquals("Wrong column name", "element2", table.getCols()[1].getId());
		
		// we should have at least 10 rows
		Assert.assertEquals("No rows", 1, table.getRows().length);
		
	}

}
