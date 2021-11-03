package com.bot.ETRA.models.active_commands;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveCommandRepository extends MongoRepository<ActiveCommand,String> {
    ActiveCommand findByNumericalValue(String numericalValue);
    ActiveCommand findByLiteralValue(String literalValue);
}
