package com.gallatinsystems.editorial.dao;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.editorial.domain.MapBalloonItemDefinition;
import com.gallatinsystems.editorial.domain.MapBalloonRowDefinition;
import com.gallatinsystems.framework.dao.BaseDAO;

@PersistenceCapable
public class MapBalloonItemDefinitionDao extends
		BaseDAO<MapBalloonItemDefinition> {
	public MapBalloonItemDefinitionDao() {
		super(MapBalloonItemDefinition.class);
	}

	public MapBalloonItemDefinition getByKey(Long id) {
		MapBalloonItemDefinition item = super.getByKey(id);
		List<MapBalloonRowDefinition> rowDefList = new MapBalloonRowDefinitionDao()
				.listByParentId(id);
		if (rowDefList != null)
			item.setRows(rowDefList);
		return item;
	}

	public MapBalloonItemDefinition save(MapBalloonItemDefinition item) {
		super.save(item);
		if (item.getRows() != null) {
			MapBalloonRowDefinitionDao mapRowDefDao = new MapBalloonRowDefinitionDao();
			for (MapBalloonRowDefinition rowItem : item.getRows()) {
				if (rowItem != null) {
					rowItem.setParentId(item.getKey().getId());
					mapRowDefDao.save(rowItem);
				}
			}
		}
		return item;
	}

	public List<MapBalloonItemDefinition> listByParentId(Long id) {
		List<MapBalloonItemDefinition> mapIdList = super.listByProperty("parentId", id, "Long");
		MapBalloonRowDefinitionDao mapRowDefDao = new MapBalloonRowDefinitionDao();
		for(MapBalloonItemDefinition item: mapIdList){
			List<MapBalloonRowDefinition> mapRowDefList = mapRowDefDao.listByParentId(item.getKey().getId());
			item.setRows(mapRowDefList);
		}
		return mapIdList; 
	}

}
