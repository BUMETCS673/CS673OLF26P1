export interface NavItem {
  label: string;
  icon: string;
  route: string;
  roles?: string[];
}

export const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
  { label: 'Products', icon: 'category', route: '/products', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
  { label: 'Inventory', icon: 'inventory_2', route: '/inventory', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
  { label: 'Sales', icon: 'point_of_sale', route: '/sales' },
  { label: 'Reports', icon: 'assessment', route: '/reports', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
  { label: 'Users', icon: 'groups', route: '/users', roles: ['ROLE_ADMIN'] },
];
