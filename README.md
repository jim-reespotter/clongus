# Clongus

## Object synchronisation tool

Clongus is designed as an identity provisioning and management (IdM) utility. It takes source and target datasources and will update the state of the target datasoure based on the source and the configs passed in.

It currently will take CSV, LDAP or SQL as a source and LDAP or SQL as a target datasource.

See the config files (in configs/) for example configs.

## Example configs:

#### task_calendar_create

Reads from a CSV file, will add/update a SQL entry (subject and ref fields) for each record in the CSV file 

#### task_department_create

Reads the same CSV file, will add/update an LDAP object (objectclass, name, DN, samaccountname) for each record in the CSV file

#### task_employee_create_delete

Reads a CSV file, will add/update/move an LDAP object (dn, objectClass, displayName, sam, password..) for each record int he CSV file

#### populate_department_group

Reads user CSV file, will add or remove group membership in LDAP


### Development

This is for reference only at this point - do not test it against production systems.

#### Features in development

- REST support
- better file reading (and writing)
- statement based SQL queries (rather than table based)
- script based attribute generator