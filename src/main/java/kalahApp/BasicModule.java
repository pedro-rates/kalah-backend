package kalahApp;

import com.google.inject.AbstractModule;

import kalahDao.BoardDao;
import kalahDao.BoardDaoImpl;

public class BasicModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(BoardDao.class).to(BoardDaoImpl.class);
	}
}
