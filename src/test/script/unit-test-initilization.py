import os

_MYSQL_HOME_ENV='MYSQL_HOME'
_INIT_SQL_PATH='../resources/db/initialization.sql'
_UNINIT_SQL_PATH='../resources/db/uninitialization.sql'
def initialize_db_for_java_unit_test():
    try:
        if os.environ[_MYSQL_HOME_ENV]:
            return os.system('mysql --user=%s --password=%s <%s' %('root','""',_INIT_SQL_PATH))
    except KeyError, e:
        return 1

def uninitialize_db_for_java_unit_test():
    try:
        if os.environ[_MYSQL_HOME_ENV]:
            return os.system('mysql --user=%s --password=%s <%s' %('root','""',_UNINIT_SQL_PATH))
    except KeyError, e:
        return 1

def usage():
    print 'Usage : python unit-test-initialzation.py <options>\nAvailable options : \n--init    Initialize DB schema for unit testing.\n--uninit    Uninitialize DB schema and delete DB user.'

if __name__ == '__main__':
    import sys
    args = sys.argv[1:]
    return_code = 255;
    if args[0] == '--init':
        return_code = initialize_db_for_java_unit_test()
    elif args[0] == '--uninit':
        return_code = uninitialize_db_for_java_unit_test()
	print "Successfully un-initialize."
    else:
        user()
        sys.exit(1)
	sys.exit(return_code)
    if return_code == 0:
        print "Operation completed."
        sys.exit(0)
    else:
        print "Operation failed."
        sys.exit(1)
    
