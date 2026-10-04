package edu.bu.metcs673.bluejay.auth.entity;
public final class PermissionTest {
    public final void testGetterSetter() {
        final var permission = new Permission();
        permission.setDescription("description1");
        permission.setId(3L);
        permission.setName("name");
        assert permission.getDescription().equals("description1");
        assert permission.getId() == 3L;
        assert permission.getName().equals("name");
    }
}