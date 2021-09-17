package TeamCamp.demo.service;

import TeamCamp.demo.domain.repository.AddressRepository;
import TeamCamp.demo.domain.repository.ProductRepository;
import TeamCamp.demo.domain.repository.TradeRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;
    private final AddressRepository addressRepository;
}
