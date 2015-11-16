package dast.model;

public interface IDastProject {
	public static final String DAST_PROJECT_INSTANCE = "dast.project.instance";

	public void addProject(Object project);

	public Object findType(String typeName);

	public Object launch();

	public String name();

	public int targetId();

}
