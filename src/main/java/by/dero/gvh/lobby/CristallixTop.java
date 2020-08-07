package by.dero.gvh.lobby;

import by.dero.gvh.utils.Pair;
import ru.cristalix.core.display.data.ColumnDrawData;
import ru.cristalix.core.display.data.DataDrawData;
import ru.cristalix.core.display.data.StringDrawData;
import ru.cristalix.core.display.data.TableDrawData;
import ru.cristalix.core.math.V2;
import ru.cristalix.core.math.V3;
import ru.cristalix.core.render.IRenderService;
import ru.cristalix.core.render.VisibilityTarget;
import ru.cristalix.core.render.WorldRenderData;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CristallixTop {
	private String name = UUID.randomUUID().toString();
	private Supplier<List<Pair<String, String>>> getter;
	private String column1, column2;
	private int rotate;
	private int topNameShift;
	private String topName;
	private V3 position;
	private UUID worldUid;
	
	private DataDrawData data;
	
	public CristallixTop(Supplier<List<Pair<String, String>>> getter, String column1, String column2, String topName, int topNameShift, int rotate, V3 position, UUID worldUid) {
		this.getter = getter;
		this.column1 = column1;
		this.column2 = column2;
		this.rotate = rotate;
		this.topName = topName;
		this.topNameShift = topNameShift;
		this.position = position;
		this.worldUid = worldUid;
		
		this.data = DataDrawData.builder().dimensions(new V2(4, 4)).scale(1).position(position).rotation(rotate).tables(getTables()).build();
		WorldRenderData worldRender = WorldRenderData.builder().visibilityTarget(VisibilityTarget.BLACKLIST).dataDrawData(data).name(name).build();
		IRenderService.get().createGlobalWorldRenderData(worldUid, name, worldRender);
		visible(true);
		
		String textDataName = UUID.randomUUID().toString();
		IRenderService.get().createGlobalWorldRenderData(worldUid, textDataName, WorldRenderData.builder().visibilityTarget(VisibilityTarget.BLACKLIST).name(textDataName).dataDrawData(DataDrawData.builder()
				.strings(Arrays.asList(
						StringDrawData.builder().align(1).scale(3).position(new V2(topNameShift, 13)).string(topName).build(),
						StringDrawData.builder().align(1).scale(2).position(new V2(175, 50)).string("(Крути колёсиком)").build()
				))
				.dimensions(new V2(4, 1))
				.scale(1)
				.position(new V3(position.getX(), position.getY() + 1, position.getZ()))
				.rotation(rotate)
				.build()).build());
		IRenderService.get().setRenderVisible(worldUid, textDataName, true);
	}
	
	public void update() {
		data.setTables(getTables());
		visible(false);
		visible(true);
	}
	
	private void visible(boolean visible) {
		IRenderService.get().setRenderVisible(worldUid, name, visible);
	}
	
	private List<TableDrawData> getTables() {
		return Collections.singletonList(TableDrawData.builder().columns(getColumns()).position(new V2(0, 0)).scale(1).dimensions(new V2(357, 0)).build());
	}
	
	private List<ColumnDrawData> getColumns() {
		List<Pair<String, String>> list = getter.get();
		List<ColumnDrawData> drawData = new ArrayList<>();
		
		drawData.add(ColumnDrawData.builder().columnName(column1).values(list.stream().map(Pair::getKey).collect(Collectors.toList())).build());
		drawData.add(ColumnDrawData.builder().columnName(column2).values(list.stream().map(Pair::getValue).collect(Collectors.toList())).build());
		
		return drawData;
	}
	
}
