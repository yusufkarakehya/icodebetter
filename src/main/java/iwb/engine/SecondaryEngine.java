package iwb.engine;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iwb.dao.rdbms_impl.SecondaryDao;

@Service
@Transactional("secondaryTransactionManager")
public class SecondaryEngine {
	@Autowired
	private SecondaryDao secondaryDao;
	
	public void test() {
		List l =  secondaryDao.executeSQLQuery("select * from foo", null);
	}
}
