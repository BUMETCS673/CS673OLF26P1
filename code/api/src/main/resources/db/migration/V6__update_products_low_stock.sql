-- No AI Usage, Manually entered

alter table products
    add low_stock int default 1 null comment 'Value which flags current stock as low in UI.';

