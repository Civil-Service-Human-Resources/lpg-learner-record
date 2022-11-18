ALTER TABLE `module_record` ADD CONSTRAINT unique_module_id_user_id UNIQUE (module_id, user_id);
