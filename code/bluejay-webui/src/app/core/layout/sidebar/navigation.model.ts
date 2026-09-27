export interface NavItem {
  label: string;
  icon: string;
  route: string;
}

export const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
  { label: 'Products', icon: 'category', route: '/products' },
  { label: 'Inventory', icon: 'inventory_2', route: '/inventory' },
  { label: 'Sales', icon: 'point_of_sale', route: '/sales' },
  { label: 'Reports', icon: 'assessment', route: '/reports' },
  { label: 'Users', icon: 'groups', route: '/users' },
];