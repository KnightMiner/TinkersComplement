package knightminer.tcomplement.shared.client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import knightminer.tcomplement.TinkersComplement;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.repository.FileRepository;

public class ModuleFileRepository extends FileRepository {

	public ModuleFileRepository(String location) {
		super(location);
	}

	@Override
	public List<SectionData> getSections() {
		// same as super, except we remove any where the module is not loaaded
		return Arrays.stream(BookLoader.GSON.fromJson(resourceToString(getResource(getResourceLocation("index.json"))), ModuleSectionData[].class))
				.filter((section)->section.module.isEmpty() || TinkersComplement.pulseManager.isPulseLoaded(section.module))
				.collect(Collectors.toList());
	}
}
