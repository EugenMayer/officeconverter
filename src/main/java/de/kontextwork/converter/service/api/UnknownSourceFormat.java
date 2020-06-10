package de.kontextwork.converter.service.api;

public class UnknownSourceFormat extends Exception
{
  public UnknownSourceFormat()
  {
  }

  public UnknownSourceFormat(final String message)
  {
    super(message);
  }

  public UnknownSourceFormat(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public UnknownSourceFormat(final Throwable cause)
  {
    super(cause);
  }

  public UnknownSourceFormat(
    final String message,
    final Throwable cause,
    final boolean enableSuppression,
    final boolean writableStackTrace
  )
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
