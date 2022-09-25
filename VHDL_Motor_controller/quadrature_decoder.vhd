Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity quadrature_decoder is
  port (
    clk : in std_logic;
    reset : in std_logic;
    sa : in std_logic;
    sb : in std_logic;
    pos_inc : out std_logic;
    pos_dec : out std_logic
  );
end entity;

architecture rtl of quadrature_decoder is
  signal err : std_logic := '0';

  type state is (s_reset, s_init, s_0, s_1, s_2, s_3);
  signal current_state : state;
  signal next_state : state;

begin

  current_state <= s_reset when reset else
                   next_state when rising_edge(clk);

  NEXT_STATE_P: process (all) is
  
  begin
    next_state <= current_state;
    case current_state is
      when s_reset =>
        next_state <= s_init;
      when s_init =>
        next_state <= s_0 when sa = '0' and sb = '0' else
                      s_1 when sa = '0' and sb = '1' else
                      s_2 when sa = '1' and sb = '1' else
                      s_3 when sa = '1' and sb = '0';
      when s_0 =>
        next_state <= s_0 when sa = '0' and sb = '0' else
                      s_1 when sa = '0' and sb = '1' else
                      s_reset when sa = '1' and sb = '1' else
                      s_3 when sa = '1' and sb = '0';
      when s_1 =>
        next_state <= s_0 when sa = '0' and sb = '0' else
                      s_1 when sa = '0' and sb = '1' else
                      s_2 when sa = '1' and sb = '1' else
                      s_reset when sa = '1' and sb = '0';
      when s_2 =>
        next_state <= s_reset when sa = '0' and sb = '0' else
                      s_1 when sa = '0' and sb = '1' else
                      s_2 when sa = '1' and sb = '1' else
                      s_3 when sa = '1' and sb = '0';
      when s_3 =>
        next_state <= s_0 when sa = '0' and sb = '0' else
                      s_reset when sa = '0' and sb = '1' else
                      s_2 when sa = '1' and sb = '1' else
                      s_3 when sa = '1' and sb = '0';
    end case;
  end process NEXT_STATE_P;

  OUTPUT_P: process(all) is
  begin
    pos_inc <= '0';
    pos_dec <= '0';
    err <= '0';

    case current_state is
      when s_0 =>
        pos_inc <= '1' when sa = '0' and sb = '1';
        pos_dec <= '1' when sa = '1' and sb = '0';
        err <= '1' when sa = '1' and sb = '1';
      when s_1 =>
        pos_inc <= '1' when sa = '1' and sb = '1';
        pos_dec <= '1' when sa = '0' and sb = '0';
        err <= '1' when sa = '1' and sb = '0';
      when s_2 =>
        pos_inc <= '1' when sa = '1' and sb = '0';
        pos_dec <= '1' when sa = '0' and sb = '1';
        err <= '1' when sa = '0' and sb = '0';
      when s_3 =>
        pos_inc <= '1' when sa = '0' and sb = '0';
        pos_dec <= '1' when sa = '1' and sb = '1';
        err <= '1' when sa = '0' and sb = '1';
      when others =>
    end case;
  end process;


  

end architecture;
