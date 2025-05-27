package com.beaconfire.coreservice.service;

import com.beaconfire.coreservice.dao.WatchlistDao;
import com.beaconfire.coreservice.domain.Watchlist;
import com.beaconfire.coreservice.domain.WatchlistProduct;
import com.beaconfire.coreservice.domain.complementary.Product;
import com.beaconfire.coreservice.dto.WatchlistDetailDto;
import com.beaconfire.coreservice.exception.NotAuthorizedException;
import com.beaconfire.coreservice.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class WatchlistService {
    private final WatchlistDao watchlistDao;
    private final AggregatorService aggregatorService;

    public WatchlistService(WatchlistDao watchlistDao, AggregatorService aggregatorService) {
        this.watchlistDao = watchlistDao;
        this.aggregatorService = aggregatorService;
    }

    public List<Watchlist> getWatchlistsByUserId(Long userId){
        return watchlistDao.findActiveByUserId(userId);
    }

    /** Get watchlist with product
     * @param watchlistId
     * @param userId
     * @return WatchlistDetailDto
     * @throws RuntimeException
     */
    @Transactional(readOnly = true)
    public WatchlistDetailDto getWatchlistById(Long watchlistId, Long userId) throws RuntimeException{
        Watchlist watchlist = watchlistDao.findById(watchlistId)
                .orElseThrow(() -> new NotFoundException("Watchlist not found"));
        if(!Objects.equals(watchlist.getUserId(), userId)){
            throw new NotAuthorizedException("You are not authorized to view this watchlist");
        }

        List<Long> productIds = watchlist.getWatchlistProducts().stream()
                .map(WatchlistProduct::getProductId)
                .toList();
        List<Product> products = aggregatorService.getProductsFromCatalogService(productIds);

        return WatchlistDetailDto.builder()
                .watchlistId(watchlist.getWatchlistId())
                .userId(watchlist.getUserId())
                .name(watchlist.getName())
                .isActive(watchlist.getIsActive())
                .updatedAt(watchlist.getUpdatedAt())
                .products(products)
                .build();
    }

    @Transactional
    public void addProductToWatchlist(Long watchlistId, Long productId, Long updatedBy) throws RuntimeException{
        Watchlist watchlist = watchlistDao.findById(watchlistId)
                .orElseThrow(() -> new NotFoundException("Watchlist not found"));
        if(!Objects.equals(watchlist.getUserId(), updatedBy)){
            throw new NotAuthorizedException("You are not authorized to add product to this watchlist");
        }
        WatchlistProduct wp = WatchlistProduct.builder()
                .watchlist(watchlist)
                .productId(productId)
                .build();
        watchlist.getWatchlistProducts().add(wp);
        watchlist.setUpdatedAt(Timestamp.from(Instant.now()));
        watchlistDao.persist(watchlist);
    }

    @Transactional
    public void removeProductFromWatchlist(Long watchlistId, Long productId, Long updatedBy) throws RuntimeException{
        Watchlist watchlist = watchlistDao.findById(watchlistId)
                .orElseThrow(() -> new NotFoundException("Watchlist not found"));
        if(!Objects.equals(watchlist.getUserId(), updatedBy)){
            throw new NotAuthorizedException("You are not authorized to add product to this watchlist");
        }
        watchlist.getWatchlistProducts().removeIf(wp -> Objects.equals(wp.getProductId(), productId));
        watchlistDao.saveOrUpdate(watchlist);
    }

    @Transactional
    public WatchlistDetailDto createWatchlist(Long userId, String name){
        Watchlist watchlist = Watchlist.builder()
                .userId(userId)
                .name(name)
                .build();
        Watchlist newWatchlist = watchlistDao.saveOrUpdate(watchlist);
        return WatchlistDetailDto.builder()
                .watchlistId(newWatchlist.getWatchlistId())
                .userId(newWatchlist.getUserId())
                .name(newWatchlist.getName())
                .isActive(newWatchlist.getIsActive())
                .updatedAt(newWatchlist.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteWatchlist(Long watchlistId, Long updatedBy){
        Watchlist watchlist = watchlistDao.findById(watchlistId)
                .orElseThrow(() -> new NotFoundException("Watchlist not found"));
        if(!Objects.equals(watchlist.getUserId(), updatedBy)){
            throw new NotAuthorizedException("You are not authorized to delete this watchlist");
        }
        watchlist.setIsActive(false);
        watchlistDao.saveOrUpdate(watchlist);
    }
}
