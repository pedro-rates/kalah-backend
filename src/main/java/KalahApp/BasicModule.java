package KalahApp;

import com.google.inject.AbstractModule;

import KalahDao.BoardDao;
import KalahDao.BoardDaoImpl;

public class BasicModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(BoardDao.class).to(BoardDaoImpl.class);
	}
}
