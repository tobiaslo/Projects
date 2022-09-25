Library IEEE;
use IEEE.std_logic_1164.all; 
use IEEE.numeric_std.all;

entity pwm is
  port (
    clk : in std_logic; --100MHz
    reset : in std_logic;
    duty_cycle : in std_logic_vector(7 downto 0);
    pwm : out std_logic
  );
end entity pwm;

architecture rtl of pwm is

begin
  process (clk, reset) is
    variable count : unsigned(15 downto 0) := (others => '0');
  begin
    if (reset = '1') then
      count := (others => '0');
    elsif rising_edge(clk) then
      count := count + 1;
    end if;

    if (count(15) = '1') then
      count := (others => '0');
      pwm <= '0';
    elsif (count(15 downto 8) < unsigned(abs(signed(duty_cycle)))) then
      pwm <= '1';
    else
      pwm <= '0';
    end if;

  end process;
end architecture;
