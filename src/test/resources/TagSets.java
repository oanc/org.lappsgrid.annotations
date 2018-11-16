import org.lappsgrid.annotations.ServiceMetadata;

@ServiceMetadata(
		name = "TagSets",
		version = "1.0.0",
		requires_tagsets = {"pos tags-pos-penntb"},
		produces_tagsets = {"ne tags-ner-stanford", "dependency tags-dep-stanford"}
)
class TagSets { }
